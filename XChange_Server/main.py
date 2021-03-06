#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2
from Handlers.RegisterHandler import RegisterHandler
from Handlers.NameHandler import NameHandler
from Handlers.GetContactsHandler import GetContactsHandler
from Handlers.SearchNearbyHandler import SearchNearbyHandler
from Handlers.GetBusinessCardHandler import GetBusinessCardHandler

app = webapp2.WSGIApplication([
    ('/register_user.*', RegisterHandler),
    ('/request_name.*', NameHandler),
    ('/get_contacts.*', GetContactsHandler),
    ('/search_nearby.*', SearchNearbyHandler),
    ('/get_businesscard.*', GetBusinessCardHandler)

], debug=True)

