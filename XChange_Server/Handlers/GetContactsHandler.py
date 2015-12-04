__author__ = 'kenlee'

import webapp2
import cgi
import json
from Handlers.UserData import User

class GetContactsHandler(webapp2.RequestHandler):
    def get(self):
        user_id = cgi.escape(self.request.get('user_id'))
        users = User.query()
        for user in users:
            if user.id == user_id:
                name = {'user_name': user.name, 'phone_number':user.phone_number,'email':user.email}
        name_json = json.dumps(name,indent=4, separators=(',', ': '))
        self.response.write(name_json)
    def post(self):
        user_id = cgi.escape(self.request.get('user_id'))
        users = User.query()
        for user in users:
            if user.id == user_id:
                name = {'user_name': user.name,'phone_number':user.phone_number,'email':user.email}
        name_json = json.dumps(name,indent=4, separators=(',', ': '))
        self.response.write(name_json)