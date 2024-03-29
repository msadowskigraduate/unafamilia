from code import interact
import discord
import requests
import json
import logging

from discord import Option


import os  # default module
from dotenv import load_dotenv

from order_services import get_orderable_items
from order_services import check_interaction_correct_user
from order_services import sort_valid_items
from order_services import is_user_authorised_to_manage_orders
from order_services import get_registration_url
from order_services import handle_order_posting

from wishlist_services import get_wishlist
from wishlist_services import process_wishlist

logging.basicConfig(format='%(asctime)s - %(levelname)s - %(message)s',
                    datefmt='%d-%b-%y %H:%M:%S', level=logging.INFO)

load_dotenv()  # load all variables from env file

bot = discord.Bot(debug_guilds=[os.getenv("GUILD")])


orderable_items = get_orderable_items()
# potions, flasks, food, vantus_runes, bandages, devices, others
if orderable_items is not None:
    item_choices = sort_valid_items(orderable_items, 115, 24)
    logging.info("item_choices")
else:
    logging.error(f'No orderable_items available to process, check wow-api')
item_subclasses = [
    discord.SelectOption(label="Potions", value="potions"),
    discord.SelectOption(label="Flasks", value="flasks"),
    discord.SelectOption(label="Food & Drinks", value="food"),
    discord.SelectOption(label="Vantus Runes", value="vantus_runes"),
    discord.SelectOption(label="Bandages", value="bandages"),
    discord.SelectOption(label="Explosives and Devices", value="devices"),
    discord.SelectOption(label="Others", value="others")
]

orders = []  # list of Order instances

# describes a set of OrderItem instances, assigned to user id


class Order():
    def __init__(self, user_id, user_display_name, character_name):
        self.__user_id = user_id
        self.__user_display_name = user_display_name
        self.__character_name = character_name
        self.__ordered_items = []
        self.__saved = False
        self.__fulfilled = False

    def save_ordered_item(self, ordered_item):
        if ordered_item["item"] is not None and ordered_item["item"]["quantity"] is not None:
            self.__ordered_items.append(ordered_item)
            # self.__saved = True
            
    def character_name(self):
        return self.__character_name 

    def get_ordered_items(self):
        return self.__ordered_items

    def get_user_id(self):
        return self.__user_id

    def get_user_display_name(self):
        return self.__user_display_name

    def get_is_order_saved(self):
        return self.__saved

    def set_order_saved(self):
        self.__saved = True

    def is_fulfilled(self):
        return self.__fulfilled

    def set_order_fulfilled(self):
        self.__fulfilled = True
        
    def prepare_and_post_order(self):
        json = "{"
        json += "\"discord_message_id\": 12345,\"discord_user_id\":"
        json += str(self.__user_id) + "," + "\"wow_user_id\":125037846,"
        json += "\"character_name\": \"" + str(self.__character_name) + "\","
        json += "\"items\":["
        
        index = 0
        for item in self.__ordered_items:
            if index == len(self.__ordered_items)-1:
                json += "{\"item_id\":" + str(item['item']['id']) + ","
                json += "\"quantity\":" + str(item['item']['quantity']) + "}"
            else:
                json += "{\"item_id\":" + str(item['item']['id']) + ","
                json += "\"quantity\":" + str(item['item']['quantity']) + "},"
            index += 1
        
        json += "]}"
        
        handle_order_posting(json, self)
        
        # logging.info(json)

# describes an item object and qty to be ordered


class OrderItem():
    def __init__(self):
        self.__item = None
        self.__qty = None

    def get_ordered_item(self):
        return {"item": self.__item}

    def set_order_item(self, item_id):
        for item in orderable_items:
            if int(item["id"]) == int(item_id):
                self.__item = item

    def set_order_item_qty(self, qty):
        # self.__qty = qty
        self.__item["quantity"] = qty


class ItemSelectionView(discord.ui.View):
    def __init__(self, order: Order, orig_ctx, current_selected_subclass=None, order_placed=False):
        super().__init__()
        self.orig_ctx = orig_ctx
        self.order = order
        self.current_selected_subclass = current_selected_subclass
        self.order_placed = order_placed

        if order_placed == True:  # if the order placed button is clicked, don't load other elements
            self.stop()
            return

        if self.current_selected_subclass is None:
            self.add_item(SubclassSelect(order=self.order,
                                         options=item_subclasses, orig_ctx=self.orig_ctx))
        else:
            #  potions, flasks, food, vantus_runes, bandages, devices, others
            if self.current_selected_subclass == "potions":
                self.add_item(ItemSelect(order=self.order,
                                         options=item_choices[0], orig_ctx=self.orig_ctx))
            elif self.current_selected_subclass == "flasks":
                self.add_item(ItemSelect(order=self.order,
                                         options=item_choices[1], orig_ctx=self.orig_ctx))
            elif self.current_selected_subclass == "food":
                self.add_item(ItemSelect(order=self.order,
                                         options=item_choices[2], orig_ctx=self.orig_ctx))
            elif self.current_selected_subclass == "vantus_runes":
                self.add_item(ItemSelect(order=self.order,
                                         options=item_choices[3], orig_ctx=self.orig_ctx))
            elif self.current_selected_subclass == "bandages":
                self.add_item(ItemSelect(order=self.order,
                                         options=item_choices[4], orig_ctx=self.orig_ctx))
            elif self.current_selected_subclass == "devices":
                self.add_item(ItemSelect(order=self.order,
                                         options=item_choices[5], orig_ctx=self.orig_ctx))
            elif self.current_selected_subclass == "others":
                self.add_item(ItemSelect(order=self.order,
                                         options=item_choices[6], orig_ctx=self.orig_ctx))
            else:
                logging.warning("Item category not recognised")
        self.add_item(CancelOrderButton(self.orig_ctx))
        self.add_item(ConfirmOrderButton(
            order=self.order, orig_ctx=self.orig_ctx))


class OrderManagementView(discord.ui.View):
    def __init__(self, order: Order):
        super().__init__()
        self.order = order

        # @discord.ui.button(label="Fulfil Order", style=discord.ButtonStyle.success)
        # async def button_callback(self, button, interaction):
        #     embeds = interaction.message.embeds
        #     if len(embeds) > 0:
        #         embed = embeds[0]
        #         embed.color = discord.Color.green()
        #         embed.title = "Order Fulfilled"
        #         self.order.set_order_fulfilled()
        #         button.disabled = True
        #         button.style = discord.ButtonStyle.secondary
        #         button.emoji = "✅"
        #         button.label = "Order Fulfilled"
        #         await interaction.response.edit_message(view=self)
        #     else:
        #         logging.error("Fulfilled order does not exist")

        if not self.order.is_fulfilled():
            self.add_item(FulfilOrderButton(order))


class FulfilOrderButton(discord.ui.Button):
    def __init__(self, order: Order):
        super().__init__(style=discord.ButtonStyle.success, label="Fulfil order", row=2)
        self.order = order

    async def callback(self, interaction):
        embeds = interaction.message.embeds
        if len(embeds) > 0:
            embed = embeds[0]
            embed.color = discord.Color.green()
            embed.title = f"✅ Order Fulfilled for {self.order.get_user_display_name()}"
            self.order.set_order_fulfilled()
            await interaction.response.edit_message(embeds=embeds, view=OrderManagementView(self.order))
        else:
            logging.error("Fulfilled order does not exist")


class CancelOrderButton(discord.ui.Button):
    def __init__(self, orig_ctx):
        super().__init__(style=discord.ButtonStyle.danger, label="Cancel Order", row=2)

        self.orig_ctx = orig_ctx

    async def callback(self, interaction: discord.Interaction):
        if await check_interaction_correct_user(interaction, self.orig_ctx):
            for order in orders:
                if order.get_user_id() == interaction.user.id and order.get_is_order_saved() == False:
                    logging.info(f'Cancelling order {order}')
                    del order
            global current_selected_item
            current_selected_item = None
            await interaction.message.delete()
            await interaction.response.send_message(
                "Order cancelled",
                ephemeral=True,
                delete_after=5.0
            )
        else:
            await interaction.response.send_message(
                "This isn't your order! Please start your own order with the /createneworder command!",
                ephemeral=True,
                delete_after=5.0
            )


class ConfirmOrderButton(discord.ui.Button):
    def __init__(self, order: Order, orig_ctx):
        super().__init__(style=discord.ButtonStyle.success, label="Confirm order", row=2)
        self.order = order
        self.orig_ctx = orig_ctx

    async def callback(self, interaction: discord.Interaction):
        print(self.order.get_ordered_items())
        json_dump = json.dumps(self.order.get_ordered_items())
        embeds = interaction.message.embeds
        if len(embeds) > 0:
            embed = embeds[0]
            embed.color = discord.Color.green()
            embed.title = "Order saved for " + interaction.user.display_name
            await interaction.message.edit(embeds=embeds, view=ItemSelectionView(orig_ctx=self.orig_ctx, order=self.order, order_placed=True))
            await interaction.response.send_message("Order placed, sending to Java:\n" + json_dump)
            self.order.prepare_and_post_order()
        else:
            logging.error("No items have been added to the order")
            await interaction.response.send_message("Your order is currently empty, please add some items", ephemeral=True)


class SubclassSelect(discord.ui.Select):
    def __init__(self, options: list[discord.SelectOption], order: Order, orig_ctx):
        super().__init__(
            placeholder="Select an item category..",
            options=options
        )
        self._orig_ctx = orig_ctx
        self.order = order

    async def callback(self, interaction: discord.Interaction):
        current_selected_subclass = self.values[0]
        await interaction.response.edit_message(view=ItemSelectionView(orig_ctx=self._orig_ctx, order=self.order, current_selected_subclass=current_selected_subclass))


class ItemSelect(discord.ui.Select):
    def __init__(self, options: list[discord.SelectOption], order: Order, orig_ctx):
        super().__init__(
            placeholder="Select an item..",
            options=options
        )
        self._orig_ctx = orig_ctx
        self.order = order
        self.options.append(discord.SelectOption(
            label="Back", value="back"))

    async def callback(self, interaction: discord.Interaction):
        global current_selected_item
        current_selected_item = self.values[0]
        if current_selected_item == "back":
            await interaction.response.edit_message(view=ItemSelectionView(orig_ctx=self._orig_ctx, order=self.order))
            for option in reversed(self.options):
                if option.value == "back":
                    self.options.pop()
                    continue
        else:
            for option in reversed(self.options):
                if option.value == "back":
                    self.options.pop()
                    continue
            orderItem = OrderItem()
            orderItem.set_order_item(self.values[0])
            modal = AddItemToOrderModal(
                title="Enter Quantity", orderItem=orderItem, orig_ctx=self._orig_ctx, order=self.order)
            await interaction.response.send_modal(modal)


# discord modal, allows user to enter qty of item to order
class AddItemToOrderModal(discord.ui.Modal):
    def __init__(self, order: Order, orderItem, orig_ctx, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)

        self.add_item(discord.ui.InputText(label="Quantity:"))
        self.orderItem = orderItem
        self._orig_ctx = orig_ctx
        self.order = order

    async def callback(self, interaction: discord.Interaction):
        self.orderItem.set_order_item_qty(self.children[0].value)
        embeds = interaction.message.embeds

        if self.order.get_user_id() == interaction.user.id and self.order.get_is_order_saved() == False:
            self.order.save_ordered_item(self.orderItem.get_ordered_item())
            if len(embeds) > 0:
                current_index_list = embeds[0].fields[0].value
                current_item_list = embeds[0].fields[1].value
                current_qty_list = embeds[0].fields[2].value

                for iteration, order_item in enumerate(self.order.get_ordered_items()):
                    embeds[0].set_field_at(
                        0, name="Index", value=current_index_list + "\n" + str(iteration+1))
                    embeds[0].set_field_at(
                        1, name="Item: ", value=current_item_list + "\n" + order_item["item"]["name"])
                    embeds[0].set_field_at(
                        2, name="Quantity: ", value=current_qty_list + "\n" + order_item["item"]["quantity"])
            else:
                embed = discord.Embed()
                embed.title = "Current order for: " + interaction.user.display_name
                for iteration, order_item in enumerate(self.order.get_ordered_items()):
                    embed.add_field(name="Index", value=str(iteration+1))
                    embed.add_field(
                        name="Item: ", value=order_item["item"]["name"])
                    embed.add_field(name="Quantity: ",
                                    value=order_item["item"]["quantity"])
                    embed.colour = discord.Color.orange()
                embeds.append(embed)
        await interaction.response.edit_message(embeds=embeds, view=ItemSelectionView(orig_ctx=self._orig_ctx, order=self.order))


@bot.event
async def on_ready():
    await bot.register_commands()
    logging.info(f"{bot.user} is ready and online")


@bot.slash_command(name="createneworder", description="Create a new order")
async def create_order(
    ctx: discord.ApplicationContext,
    character: Option(str, "Character name"),
    realm: Option(str, "Realm")
):
    character_name = character + "-" + realm
    order = Order(ctx.user.id, ctx.user.display_name, character_name)
    orders.append(order)
    await ctx.send_response("Pick an item to add to your order:", view=ItemSelectionView(order=order, orig_ctx=ctx))


""" getorders command temporary for demo purposes. Final version will display data returned from
    Java api of orders saved to db """


@bot.slash_command(name="getoutstandingorders", description="Get a list of outstanding orders")
async def get_outstanding_orders(ctx: discord.ApplicationContext):
    if not await is_user_authorised_to_manage_orders(ctx):
        await ctx.send_response("You don't have permission to use this command", ephemeral=True)
        return
    if len(orders) < 1:
        logging.info('No orders registered yet')
        await ctx.send_response("There are currently no orders to view", ephemeral=True)
        return
    embeds = []
    for order in orders:
        if not order.is_fulfilled():
            embed = discord.Embed(
                title=f"Order for user {order.get_user_display_name()}")
            embed.color = discord.Color.orange()
            order_items = order.get_ordered_items()
            for order_item in order_items:
                embed.add_field(
                    name="Item: ", value=order_item["item"]["name"])
                embed.add_field(name="Quantity: ",
                                value=order_item["item"]["quantity"])
            embeds.append(embed)
            await ctx.respond(embed=embed, view=OrderManagementView(order))

""" getcompletedorders will need filtering options based on date/number of most recent results/search by name
    filtering will probably need to happen on java side"""


@bot.slash_command(name="getcompletedorders", description="Get a list of fulfilled orders")
async def get_fulfilled_orders(ctx: discord.ApplicationContext):
    if not await is_user_authorised_to_manage_orders(ctx):
        await ctx.send_response("You don't have permission to use this command", ephemeral=True)
        return
    if len(orders) < 1:
        logging.info('No orders registered yet')
        await ctx.send_response("There are currently no orders to view", ephemeral=True)
        return
    embeds = []
    for order in orders:
        if order.is_fulfilled():
            embed = discord.Embed(
                title=f"Order for user {order.get_user_display_name()}")
            embed.color = discord.Color.green()
            order_items = order.get_ordered_items()
            for order_item in order_items:
                embed.add_field(
                    name="Item: ", value=order_item["item"]["name"])
                embed.add_field(name="Quantity: ",
                                value=order_item["item"]["quantity"])
            embeds.append(embed)
            await ctx.respond(embed=embed, view=OrderManagementView(order))


@bot.slash_command(name="link", description="Link your discord account to your WoW account")
async def link_account(ctx: discord.ApplicationContext):
    registration_url = get_registration_url(ctx.author.id)
    await ctx.respond("Click the link below to connect your account:\n" + registration_url, ephemeral=True)

@bot.slash_command(name="wishlist", description="run the wishlist command")
async def wishlist_reminders(ctx: discord.ApplicationContext):
    await ctx.respond("Working on it, this may take some time..", ephemeral=True)
    wishlist_report = get_wishlist()
    process_wishlist(wishlist_report, "Castle Nathria", "mythic")
    

    
bot.run(os.getenv("TOKEN"))  # run bot using token
