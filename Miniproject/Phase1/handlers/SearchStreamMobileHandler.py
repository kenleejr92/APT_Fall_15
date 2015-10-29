__author__ = 'kenlee'

import webapp2
import jinja2
import cgi
import json
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.api import users
from handlers.Stream import Stream
from Stream import Photo
from BaseHandler import BaseHandler

#/search_streams
class SearchStreamMobileHandler(webapp2.RequestHandler):
    def get(self):
        query_string = cgi.escape(self.request.get('query_string'))
        queryStreams = Stream.query(Stream.name == query_string)
        images = {'stream_names': [], 'image_urls': []}
        for stream in queryStreams:
            images['stream_names'].append(stream.name)
            images['image_urls'].append(stream.cover_image)

        image_json = json.dumps(images,indent=4, separators=(',', ': '))
        self.response.write(image_json)