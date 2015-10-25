__author__ = 'kenlee'

import webapp2
import json
from Stream import Stream
from Stream import Photo
import cgi
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api import images

class ViewPhotosMobileHandler(webapp2.RequestHandler):
    def get(self):

        #Get the list of streams
        stream_name = cgi.escape(self.request.get('stream_name'))
        stream_key = ndb.Key(Stream,stream_name)
        stream = stream_key.get()

        image_urls = {'image_urls': []}
        photos = stream.photos
        for photo in photos:
            image_urls['image_urls'].append(photo.url)


        image_json = json.dumps(image_urls,indent=4, separators=(',', ': '))
        self.response.write(image_json)