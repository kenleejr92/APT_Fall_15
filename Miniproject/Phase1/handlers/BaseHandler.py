__author__ = 'shreyas'
import webapp2
import jinja2
import json
import os
from Stream import Stream
from Stream import Photo
from google.appengine.api import users

class BaseHandler():
    def cache(self, currentTab):
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
        taglist = []

        streamNames = []
        for stream in streams:
            streamNames.append(str(stream.name))
            for tag in stream.tags:
                if tag not in taglist:
                    streamNames.append(tag)
                    taglist.append(tag)


        template_values = {
            'streams':streams
        }

        template = JINJA_ENVIRONMENT.get_template('Search.html')
        self.response.write(template.render(streams = json.dumps(streamNames)))

        #test search header
        searchHead = JINJA_ENVIRONMENT.get_template('Header.html')
        self.response.write(searchHead.render(current = currentTab))

    def errorpage(self, msg):
        self.cache('')
        self.response.write(msg)
