__author__ = 'kenlee'

import webapp2
import jinja2
import cgi
from Stream import Stream
from google.appengine.api import users
from google.appengine.ext import ndb
from google.appengine.api import images

class ViewMapHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        stream_name = cgi.escape(self.request.get('stream_name'))
        stream_key = ndb.Key(Stream,stream_name)
        stream = stream_key.get()
        photo_keys = stream.photos
        photo_urls = []
        for key in photo_keys:
            photo_urls.append(images.get_serving_url(key))

        template_values = {
            "photo_urls": photo_urls
        }
        template = JINJA_ENVIRONMENT.get_template('ViewMapPage.html')
        self.response.write(template.render(template_values))