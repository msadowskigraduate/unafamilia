from code import interact
import discord
import requests
import json


from discord import Option


import os  # default module
from dotenv import load_dotenv

from services import get_orderable_items
from services import check_interaction_correct_user
from services import sort_valid_items

load_dotenv()  # load all variables from env file

bot = discord.Bot(debug_guilds=[os.getenv("GUILD")])


orderable_items = get_orderable_items()
# potions, flasks, food, vantus_runes, bandages, devices, others
item_choices = sort_valid_items(orderable_items, 115, 25)
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
    def __init__(self, user_id):
        self.__user_id = user_id
        self.__ordered_items = []
        self.__saved = False

    def save_ordered_item(self, ordered_item):
        if ordered_item["item"] is not None and ordered_item["item"]["quantity"] is not None:
            self.__ordered_items.append(ordered_item)
            # self.__saved = True

    def get_ordered_items(self):
        return self.__ordered_items

    def get_user_id(self):
        return self.__user_id

    def get_is_order_saved(self):
        return self.__saved

    def set_order_saved(self):
        self.__saved = True

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
            return

        if self.current_selected_subclass is None:
            self.add_item(SubclassSelect(order=self.order,
                                         options=item_subclasses, orig_ctx=self.orig_ctx))
        else:
            print(self.current_selected_subclass)
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
                print("Item category not recognised")
        self.add_item(CancelOrderButton(self.orig_ctx))
        self.add_item(ConfirmOrderButton(
            order=self.order, orig_ctx=self.orig_ctx))


class CancelOrderButton(discord.ui.Button):
    def __init__(self, orig_ctx):
        super().__init__(style=discord.ButtonStyle.danger, label="Cancel Order", row=2)

        self.orig_ctx = orig_ctx

    async def callback(self, interaction: discord.Interaction):
        if await check_interaction_correct_user(interaction, self.orig_ctx):
            for order in orders:
                if order.get_user_id() == interaction.user.id and order.get_is_order_saved() == False:
                    print(order)
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
            embed.title = "Order saved for " + interaction.user.mention
            await interaction.message.edit(embeds=embeds, view=ItemSelectionView(orig_ctx=self.orig_ctx, order=self.order, order_placed=True))
            await interaction.response.send_message("Order placed, sending to Java:\n" + json_dump)
        else:
            print("Nyly tried to trigger the same bug again, silly donkey")
            pass


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
        for order in orders:
            if order.get_user_id() == interaction.user.id and order.get_is_order_saved() == False:
                order.save_ordered_item(self.orderItem.get_ordered_item())
                i = 1
                if len(embeds) > 0:
                    current_index_list = embeds[0].fields[0].value
                    current_item_list = embeds[0].fields[1].value
                    current_qty_list = embeds[0].fields[2].value

                    for iteration, order_item in enumerate(order.get_ordered_items()):
                        embeds[0].set_field_at(
                            0, name="Index", value=current_index_list + "\n" + str(iteration+1))
                        embeds[0].set_field_at(
                            1, name="Item: ", value=current_item_list + "\n" + order_item["item"]["name"])
                        embeds[0].set_field_at(
                            2, name="Quantity: ", value=current_qty_list + "\n" + order_item["item"]["quantity"])
                else:
                    embed = discord.Embed()
                    embed.title = "Current order for: " + interaction.user.mention
                    for iteration, order_item in enumerate(order.get_ordered_items()):
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
    print(f"{bot.user} is ready and online!")
    await bot.register_commands()


@bot.slash_command(name="createneworder", description="Create a new order")
async def create_order(
    ctx: discord.ApplicationContext,
):
    order = Order(ctx.user.id)
    orders.append(order)
    await ctx.send_response("Pick an item to add to your order:", view=ItemSelectionView(order=order, orig_ctx=ctx))


@bot.slash_command(name="getorders", description="Get a list of outstanding orders")
async def get_orders(ctx: discord.ApplicationContext):
    embeds = []
    for order in orders:
        order_items = order.get_ordered_items()
        for order_item in order_items:
            embed = discord.Embed(
                title=f"Order for user {order.get_user_id()}")
            embed.add_field(name="Item: ", value=order_item["item"]["name"])
            embed.add_field(name="Quantity: ", value=order_item["quantity"])
            embeds.append(embed)

            await ctx.send_response(embeds=embeds)


# @bot.slash_command(name="createitem", description="Send request to JavaBoi")
# async def create_item(ctx):
#     r = requests.post('http://localhost:9000/item',
#                       json={"item_id": 172042,
#                             "max_quantity": 20,
#                             "slug": "surprisingly-palatable-feast"})
#     print(f"Status Code: {r.status_code}")
#     await ctx.respond(f"Response: {r.status_code}")


bot.run(os.getenv("TOKEN"))  # run bot using token
