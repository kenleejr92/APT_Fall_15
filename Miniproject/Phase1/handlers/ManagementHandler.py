__author__ = 'kenlee'

import webapp2
import jinja2
from Stream import Stream
from google.appengine.api import users

class ManagementHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()
        user_id = user.user_id()
        logout_url = users.create_logout_url('/')

        #Get the list of streams
        my_streams = Stream.query(Stream.owner_id == user_id)
        subscribed_streams = Stream.query(Stream.subscribed_users.IN([user_id]))
        template_values = {
            'user':user,
            'logout_url':logout_url,
            'my_streams':my_streams,
            'subscribed_streams':subscribed_streams
        }

        template = JINJA_ENVIRONMENT.get_template('ManagementPage.html')
        self.response.write(template.render(template_values))