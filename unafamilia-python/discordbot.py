from code import interact
import discord
import requests


from discord.commands import Option


import os  # default module
from dotenv import load_dotenv

from services import get_orderable_items
from services import check_interaction_correct_user

load_dotenv()  # load all variables from env file

bot = discord.Bot(debug_guilds=[os.getenv("GUILD")])

orderable_items = get_orderable_items()
item_choices = []
current_selected_item = None

# takes string slug, outputs capitalised string with spaces
def capitalise_slug(slug):
    capitalised_slug = " ".join(word[0].upper()+word[1:]
                                for word in slug.split("-"))
    return capitalised_slug


# converts item slugs into capitalised strings, appends to item_choices
for item in orderable_items:
    item_choices.append(discord.SelectOption(
        label=capitalise_slug(item["slug"]), value=str(item["item_id"])))

orders = []  # list of Order instances

# describes a set of OrderItem instances, assigned to user id
class Order():
    def __init__(self, user_id):
        self.__user_id = user_id
        self.__ordered_items = []
        self.__saved = False

    def save_ordered_item(self, ordered_item):
        if ordered_item["item"] is not None and ordered_item["quantity"] is not None:
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
        return {"item": self.__item, "quantity": self.__qty}

    def set_order_item(self, item_id):
        for item in orderable_items:
            if int(item["item_id"]) == int(item_id):
                self.__item = item

    def set_order_item_qty(self, qty):
        self.__qty = qty

# # discord view - selection box containing item_choices
# class ItemSelectionView(discord.ui.View):
#     @discord.ui.select(
#         placeholder="Item to be ordered",
#         options=item_choices
#     )
#     async def select_callback(self, select, interaction):
#         orderItem = OrderItem()
#         orderItem.set_order_item(select.values[0])
#         print(orderItem.get_ordered_item)
#         modal = AddItemToOrderModal(
#             title="Enter Quantity", orderItem=orderItem)
#         await interaction.response.send_modal(modal)

class ItemSelectionView(discord.ui.View):
    def __init__(self, orig_ctx=None):
        super().__init__()
        self.orig_ctx=orig_ctx

        self.add_item(ItemSelect(options=item_choices))
        self.add_item(AddMoreItemsButton(self.orig_ctx))
        self.add_item(CancelOrderButton(self.orig_ctx))
        self.add_item(ConfirmOrderButton())
        

   
class AddMoreItemsButton(discord.ui.Button):
    def __init__(self, orig_ctx=None):
        super().__init__(style=discord.ButtonStyle.primary, label="Add more items", row=2)
        
        self.orig_ctx=orig_ctx
        
    async def callback(self, interaction: discord.Interaction):
        if check_interaction_correct_user(interaction, self.orig_ctx):
            
            await interaction.message.edit(view=None, content="Need to add logic")
            interaction
        else:
            await interaction.response.send_message("This isn't your order! Use the command to start your own :)")
            
        
class CancelOrderButton(discord.ui.Button):
    def __init__(self, orig_ctx=None):
        super().__init__(style=discord.ButtonStyle.danger, label="Cancel Order", row=2)
        
        self.orig_ctx=orig_ctx
        
    async def callback(self, interaction: discord.Interaction):
        if await check_interaction_correct_user(interaction, self.orig_ctx):
        
            for order in orders:
                if order.get_user_id() == interaction.user.id and order.get_is_order_saved() == False:
                    print(order)
                    del order
            global current_selected_item
            current_selected_item=None
            # await interaction.message.edit(view=None, embed=None, content="Order cancelled")
            await interaction.user.send(view=None, embed=None, content="Fuck off Nyly")
        else:
            await interaction.user.send(view=None, embed=None, content="Fuck off Nyly")
            print("Told Nyly to fuck off")
                            
    
class ConfirmOrderButton(discord.ui.Button):
    def __init__(self):
        super().__init__(style=discord.ButtonStyle.success, label="Confirm order", row=2)
        
    async def callback(self, interaction: discord.Interaction):
        await interaction.response.send_message("Order placed")
        
   
        
class ItemSelect(discord.ui.Select):
    def __init__(self, options: list[discord.SelectOption]):
        super().__init__(
                placeholder="Select an item..",
                options=options
            )
    
    async def callback(self, interaction: discord.Interaction):
        global current_selected_item
        current_selected_item = self.values[0]
        orderItem = OrderItem()
        orderItem.set_order_item(self.values[0])
        # print(orderItem.get_ordered_item)
        modal = AddItemToOrderModal(
            title="Enter Quantity", orderItem=orderItem)
        await interaction.response.send_modal(modal)
    

# discord modal, allows user to enter qty of item to order
class AddItemToOrderModal(discord.ui.Modal):
    def __init__(self, orderItem, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)

        self.add_item(discord.ui.InputText(label="Quantity:"))
        self.orderItem = orderItem

    async def callback(self, interaction: discord.Interaction):
        self.orderItem.set_order_item_qty(self.children[0].value)
        embeds=interaction.message.embeds
        for order in orders:
            if order.get_user_id() == interaction.user.id and order.get_is_order_saved() == False:
                order.save_ordered_item(self.orderItem.get_ordered_item())
                print(interaction.message.embeds)
                embed = discord.Embed(title="Current order:")
                for order_item in order.get_ordered_items():
                    embed.add_field(name="Item: ", value=capitalise_slug(
                        order_item["item"]["slug"]))
                    embed.add_field(name="Quantity: ",
                                    value=order_item["quantity"])
                embeds.append(embed)
            else:
                print("Something's not right")
                print(f"{order.get_user_id()} does not equal {interaction.user.id}")
                print(order.get_is_order_saved())
        self.stop()
        await interaction.message.edit(embeds=embeds, view=ItemSelectionView())


@bot.event
async def on_ready():
    print(bot.guilds)
    print(f"{bot.user} is ready and online!")
    await bot.register_commands()



        
@bot.slash_command(name="createneworder", description="Create a new order")
async def create_order(
    ctx: discord.ApplicationContext,
):
    order = Order(ctx.user.id)
    orders.append(order)

    await ctx.send_response("Pick an item to add to your order:", view=ItemSelectionView(orig_ctx=ctx))
    
    


@bot.slash_command(name="getorders", description="Get a list of outstanding orders")
async def get_orders(ctx: discord.ApplicationContext):
    embeds = []
    for order in orders:
        order_items = order.get_ordered_items()
        for order_item in order_items:
            embed = discord.Embed(
                title=f"Order for user {order.get_user_id()}")
            embed.add_field(name="Item: ", value=capitalise_slug(
                order_item["item"]["slug"]))
            embed.add_field(name="Quantity: ", value=order_item["quantity"])
            embeds.append(embed)

            await ctx.send_response(embeds=embeds)


@bot.slash_command(name="createitem", description="Send request to JavaBoi")
async def create_item(ctx):
    r = requests.post('http://localhost:9000/item',
                      json={"item_id": 172042,
                            "max_quantity": 20,
                            "slug": "surprisingly-palatable-feast"})
    print(f"Status Code: {r.status_code}")
    await ctx.respond(f"Response: {r.status_code}")


bot.run(os.getenv("TOKEN"))  # run bot using token
