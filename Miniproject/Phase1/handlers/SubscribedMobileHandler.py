__author__ = 'kenlee'

import webapp2
import jinja2
import json
from Stream import Stream
from Stream import Photo
from google.appengine.api import users
import cgi

class SubscribedMobileHandler(webapp2.RequestHandler):
    def get(self):

        user_email = cgi.escape(self.request.get('user_email'))


        #Get the list of streams
        subscribed_streams = Stream.query(Stream.subscribed_users.IN([user_email]))
        images = {'stream_names': [], 'image_urls': [],}
        for stream in subscribed_streams:
            images['stream_names'].append(stream.name)
            images['image_urls'].append(stream.cover_image)

        image_json = json.dumps(images,indent=4, separators=(',', ': '))
        self.response.write(image_json)