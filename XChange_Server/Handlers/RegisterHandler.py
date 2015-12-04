__author__ = 'kenlee'
import webapp2

class RegisterHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('Register Handler!')