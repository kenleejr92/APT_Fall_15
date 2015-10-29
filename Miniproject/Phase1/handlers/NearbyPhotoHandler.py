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
class NearbyPhotoHandler(webapp2.RequestHandler):
    def get(self):

        latitude = cgi.escape(self.request.get('latitude'))
        longitude = cgi.escape(self.request.get('longitude'))
        lat_min = float(latitude) - 20.0
        lat_max = float(latitude) + 20.0
        lng_min = float(longitude) - 20.0
        lng_max = float(longitude) + 20.0
        allStreams = Stream.query()
        images = {'stream_names': [], 'image_urls': []}
        for stream in allStreams:
            photos_in_stream = stream.photos
            # for photo in photos_in_stream:
            #     images['image_urls'].append(photo.url)
            stream_name = stream.name
            for photo in photos_in_stream :
                if photo.lat > lat_min and photo.lat < lat_max and photo.lng > lng_min and photo.lng < lng_max :
                    images['image_urls'].append(photo.url)
                    images['stream_names'].append(stream_name)

        image_json = json.dumps(images,indent=4, separators=(',', ': '))
        self.response.write(image_json)