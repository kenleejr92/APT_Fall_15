__author__ = 'kenlee'

import webapp2
import jinja2
import json
from Stream import Stream
from google.appengine.api import users

class ManagementHandler(webapp2.RequestHandler):
    def setup(self, currentTab):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()
        user_id = user.user_id()
        logout_url = users.create_logout_url('/')

        #welcome to Connexus
        userInfo = {
            'user':user,
            'logout_url': logout_url
        }

        template = JINJA_ENVIRONMENT.get_template('Welcome.html')
        self.response.write(template.render(userInfo))

        #Get the list of streams
        streams = Stream.query()

        streamNames = []
        for stream in streams:
            streamNames.append(str(stream.name))

        template_values = {
            'streams':streams
        }

        template = JINJA_ENVIRONMENT.get_template('Search.html')
        self.response.write(template.render(streams = json.dumps(streamNames)))

        #test search header
        searchHead = JINJA_ENVIRONMENT.get_template('Header.html')
        self.response.write(searchHead.render(current = currentTab))

    def get(self):
        self.setup('manage')

        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()
        user_id = user.user_id()

        #Get the list of streams
        my_streams = Stream.query(Stream.owner_id == user_id)
        subscribed_streams = Stream.query(Stream.subscribed_users.IN([user_id]))
        template_values = {
            'my_streams':my_streams,
            'subscribed_streams':subscribed_streams
        }

        template = JINJA_ENVIRONMENT.get_template('ManagementPage.html')
        self.response.write(template.render(template_values))