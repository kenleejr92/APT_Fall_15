__author__ = 'kenlee'
import webapp2
import cgi
from google.appengine.ext import ndb
from Handlers.UserData import User

class RegisterHandler(webapp2.RequestHandler):
    def get(self):
        pass
    def post(self):
        user_name = cgi.escape(self.request.get('user_name'))
        user_id = cgi.escape(self.request.get('user_id'))
        user_phone_number = cgi.escape(self.request.get('phone_number'))
        user_email = cgi.escape(self.request.get('email'))
        user = User(name=user_name, id=user_id,phone_number=user_phone_number,email=user_email)
        user.key = ndb.Key(User, user_id)
        user.put()
