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

# appends valid items to item_choices - limit to 25 discord restriction


def sort_valid_items(orderable_items, min_lvl, max_items_in_list):
    item_subclasses = ["Potion",
                       "Flask",
                       "Food & Drink",
                       "Vantus Rune",
                       "Bandage",
                       "Explosives and Devices",
                       "Other"]

    potions = []
    flasks = []
    food = []
    vantus_runes = []
    bandages = []
    devices = []
    others = []
    min_lvl = min_lvl
    max_items_in_list = max_items_in_list

    for item in orderable_items:
        if item["subclass"] not in item_subclasses:
            print("No matching subclass for " + item["name"])
            continue
        elif item["level"] < 115:
            print(item["name"] + " is level " + str(item["level"]) +
                  ". Minimum level is set to " + str(min_lvl))
            continue
        elif item["subclass"] == "Potion" and len(potions) < max_items_in_list:
            potions.append(discord.SelectOption(
                label=item["name"], value=str(item["id"])))
        elif item["subclass"] == "Flask" and len(flasks) < max_items_in_list:
            flasks.append(discord.SelectOption(
                label=item["name"], value=str(item["id"])))
        elif item["subclass"] == "Food & Drink" and len(food) < max_items_in_list:
            food.append(discord.SelectOption(
                label=item["name"], value=str(item["id"])))
        elif item["subclass"] == "Vantus Rune" and len(vantus_runes) < max_items_in_list:
            vantus_runes.append(discord.SelectOption(
                label=item["name"], value=str(item["id"])))
        elif item["subclass"] == "Bandage" and len(bandages) < max_items_in_list:
            bandages.append(discord.SelectOption(
                label=item["name"], value=str(item["id"])))
        elif item["subclass"] == "Explosives and Devices" and len(devices) < max_items_in_list:
            devices.append(discord.SelectOption(
                label=item["name"], value=str(item["id"])))
        elif item["subclass"] == "Other" and len(others) < max_items_in_list:
            others.append(discord.SelectOption(
                label=item["name"], value=str(item["id"])))
        else:
            print(item["subclass"] +
                  " can hold max 25 items, skipping " + item["name"])

    return potions, flasks, food, vantus_runes, bandages, devices, others


# return bool - is interacting user = user who used original command
async def check_interaction_correct_user(interaction, orig_ctx):
    if interaction.user.id == orig_ctx.author.id:
        return True
    return False
