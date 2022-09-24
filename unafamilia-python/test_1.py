from msilib.schema import Class
import string
import requests
from unittest.mock import Mock, patch

# third party imports
import pytest 

# local imports
from services import get_orderable_items

# def func(x):
#     return x + 1

# def test_answer():
#     assert func(3) == 4
    
# def f():
#     raise SystemExit(1)

# def test_mytest():
#     with pytest.raises(SystemExit):
#         f()
        
# class TestClass:
#     def test_one(self):
#         x = "this"
#         assert "h" in x
        
    # def test_two(self):
    #     x = "hello"
    #     assert hasattr(x, "check")

# def test_request_response():
#    # Call the service, which will send a request to the server.
#     response = get_todos()
    
#     # If the request is sent successfully, then I expect a response to be returned.
#     assert response is not None, "services.get_todos returned no response"
    
# @patch('services.requests.get')
# def test_getting_todos(mock_get):
#     # Configure the mock to return a response with an OK status code.
#     mock_get.return_value.ok = True

#     # Call the service, which will send a request to the server.
#     response = get_todos()

#     # If the request is sent successfully, then I expect a response to be returned.
#     assert response is not None, "services.get_todos returned no response"

class TestDummyGetItemsClass:
    def test_object_returned(self):
        assert get_orderable_items() is not None
    
    def test_first_object_value(self):
        items_dict = get_orderable_items()
        assert items_dict[0]["item_id"] == 171270
    