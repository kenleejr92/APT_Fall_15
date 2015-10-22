__author__ = 'kenlee'

import webapp2
import json
from Stream import Stream
from google.appengine.api import users

class ViewStreamsMobileHandler(webapp2.RequestHandler):
    def get(self):

        #Get the list of streams
        images = {'stream_names': [], 'image_urls': []}
        streams = Stream.query().order(-Stream.date_last_added)
        for stream in streams:
            images['stream_names'].append(stream.name)
            images['image_urls'].append(stream.cover_image)

        image_json = json.dumps(images,indent=4, separators=(',', ': '))
        self.response.write(image_json)