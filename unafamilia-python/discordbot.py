from code import interact
import discord
import requests


from discord.commands import Option


import os #default module
from dotenv import load_dotenv

from services import get_orderable_items

load_dotenv() # load all variables from env file

bot = discord.Bot(debug_guilds=[os.getenv("GUILD")])

orderable_items = get_orderable_items()
item_choices = []

def capitalise_slug(slug):
    capitalised_slug = " ".join(word[0].upper()+word[1:] for word in slug.split("-"))
    return capitalised_slug

for item in orderable_items:
    item_choices.append(discord.SelectOption(label=capitalise_slug(item["slug"]), value=str(item["item_id"])))

orders = []

class Order():
    def __init__(self, user_id):
        self.__user_id = user_id
        self.__ordered_items = []
        self.__saved = False
        
    def save_ordered_item(self, ordered_item):
        if ordered_item["item"] is not None and ordered_item["quantity"] is not None:
            self.__ordered_items.append(ordered_item)
            self.__saved = True
            
    def get_ordered_items(self):
        return self.__ordered_items
    
    def get_user_id(self):
        return self.__user_id
    
    def get_is_order_saved(self):
        return self.__saved
    

            
    
class OrderItem():
    def __init__(self):
        self.__item = None
        self.__qty = None
    
    def get_ordered_item(self):
        return {"item":self.__item, "quantity":self.__qty}
        
    def set_order_item(self, item_id):
        for item in orderable_items:
            if int(item["item_id"]) == int(item_id):
                self.__item = item        
        
    def set_order_item_qty(self, qty):
        self.__qty = qty
        
       
class ItemSelectionView(discord.ui.View):
    @discord.ui.select(
        placeholder = "Item to be ordered", 
        options = item_choices
    )
    
    async def select_callback(self, select, interaction):
        # await interaction.response.send_message("How many?", view=QtySelectionView())
        orderItem = OrderItem()
        orderItem.set_order_item(select.values[0])
        print(orderItem.get_ordered_item)
        modal = AddItemToOrderModal(title="Enter Quantity", orderItem=orderItem)
        await interaction.response.send_modal(modal)
        # item = select.values[0]
        # print(item)
        

class AddOrConfirmView(discord.ui.View):
    @discord.ui.button(label="Add more items", style=discord.ButtonStyle.primary)
    async def add_items_button_callback(self, button, interaction):
        order = Order(interaction.user.id)
        orders.append(order)
        print(order.get_user_id())
        await interaction.response.send_message("Pick an item to add to your order:", view=ItemSelectionView())
        
    # @discord.ui.button(label="Cancel order", style=discord.ButtonStyle.danger)
    # @discord.ui.button(label="Complete order", style=discord.ButtonStyle.success)
    
    
    

class AddItemToOrderModal(discord.ui.Modal):
    def __init__(self, orderItem, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)
        
        self.add_item(discord.ui.InputText(label="Quantity:"))
        self.orderItem = orderItem

    async def callback(self, interaction: discord.Interaction):
        self.orderItem.set_order_item_qty(self.children[0].value)
        # self.orderItem.save_ordered_item(self.ordered_item, interaction.user.id)
        for order in orders:
            if order.get_user_id() == interaction.user.id and order.get_is_order_saved() == False:
                print("Matching order found")
                order.save_ordered_item(self.orderItem.get_ordered_item())
                print(order.get_ordered_items())
                embed = discord.Embed(title = "Current order:")
                for order_item in order.get_ordered_items():
                    embed.add_field(name="Item: ", value=capitalise_slug(order_item["item"]["slug"]))
                    embed.add_field(name="Quantity: ", value=order_item["quantity"])
        
        await interaction.response.send_message(embeds=[embed], view=AddOrConfirmView())
        
        
       

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
        print(order.get_user_id())
        await ctx.send("Pick an item to add to your order:", view=ItemSelectionView())
        
        
@bot.slash_command(name="getorders", description="Get a list of outstanding orders")
async def get_orders(ctx: discord.ApplicationContext):
        embeds = []
        for order in orders:
            order_items = order.get_ordered_items()
            for order_item in order_items:
                embed = discord.Embed(title=f"Order for user {order.get_user_id()}")
                embed.add_field(name="Item: ", value=capitalise_slug(order_item["item"]["slug"]))
                embed.add_field(name="Quantity: ", value=order_item["quantity"])
                embeds.append(embed)
                
                await ctx.send_response(embeds=embeds)
            

@bot.slash_command(name = "createitem", description = "Send request to JavaBoi")
async def create_item(ctx):
    # await ctx.respond("Hey!")
    r = requests.post('http://localhost:9000/item', 
                      json={"item_id": 172042,
                            "max_quantity": 20,
                            "slug": "surprisingly-palatable-feast"})
    print(f"Status Code: {r.status_code}")
    await ctx.respond(f"Response: {r.status_code}")
    

bot.run(os.getenv("TOKEN")) #run bot using token


