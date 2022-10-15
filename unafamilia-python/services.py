from urllib.parse import urljoin
import json
import discord

# Third-party imports...
import requests

# Local imports...
from constants import BASE_API_URL

ORDERABLE_ITEMS_URL = urljoin(BASE_API_URL, '/consumables')


def get_orderable_items():
    
    response = requests.get(ORDERABLE_ITEMS_URL)
    if response.ok:
        items_dict = json.loads(json.dumps(response.json()))
        items_dict = items_dict["results"]
        return items_dict
    else:
        return None
    
    # # Temporaray code until API is running:
    # items_string =  '''[
    #                     {
    #                         "item_id":171270, 
    #                         "max_quantity":20,
    #                         "slug":"potion-of-spectral-agility"
    #                     },
    #                     {
    #                         "item_id":171273, 
    #                         "max_quantity":20,
    #                         "slug":"potion-of-spectral-intellect"
    #                     }
    #                 ]'''
    
    # items_dict = json.loads(items_string)
    # if items_dict:
    #     print(items_dict)
    #     return items_dict
    # else:
    #     return None
    
# return bool - is interacting user = user who used original command    
async def check_interaction_correct_user(interaction, orig_ctx):
    if interaction.user.id == orig_ctx.author.id:
        return True
    return False