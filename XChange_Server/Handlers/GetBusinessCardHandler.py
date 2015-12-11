__author__ = 'kenlee'

import webapp2
import cgi
import json
from Handlers.UserData import User

class GetBusinessCardHandler(webapp2.RequestHandler):
    def post(self):
        user_name = cgi.escape(self.request.get('user_name'))
        users = User.query(User.name == user_name)
        for user in users:
            name = {'bc_url': user.bc_url}
        name_json = json.dumps(name,indent=4, separators=(',', ': '))
        self.response.write(name_json)