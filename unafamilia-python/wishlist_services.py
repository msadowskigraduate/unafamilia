from urllib.parse import urljoin
import json
import logging

import requests

from constants import WISHLIST_API_URL

GET_WISHLIST_URL = urljoin(WISHLIST_API_URL, 'v1/report')

def get_wishlist():
    try:
        response = requests.get(GET_WISHLIST_URL)
        if response.ok:
            logging.info(f"Response from wishlist-api: {response.status_code}")
            wishlist_dict = json.loads(json.dumps(response.json()))
            logging.info("Wishlist fetched successfully")
            # wishlist_dict = items_dict["results"]
            return wishlist_dict
        else:
            logging.error(f"Response from wishlist-api: {response.status_code}")
            return None
    except requests.exceptions.ConnectionError:
        logging.error(f"wishlist-api connection refused - check it is running")
        return None
    
    
def process_wishlist(wishlist_report, raid_name, difficulty):
    players_with_issues = []
    flg_issue_detected = False
    for player in wishlist_report:
        for issue in player["issues"]:
            if issue["instance_name"] == raid_name and issue["difficulty"] == difficulty:
                flg_issue_detected = True
                break
        
        if flg_issue_detected == True:
            issue_player = {"name": player["name"], "discord_user_id": player["discord_user_id"]}            
            players_with_issues.append(issue_player)
        flg_issue_detected = False  
        
    logging.info(players_with_issues)                          
