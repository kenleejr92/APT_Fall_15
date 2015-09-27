__author__ = 'kenlee'

import cgi
import jinja2
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api import images
from Stream import Stream

#/view_stream/stream_name
class ViewStreamHandler(blobstore_handlers.BlobstoreDownloadHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        stream_name = cgi.escape(self.request.get('stream_name'))
        stream_key = ndb.Key(Stream,stream_name)
        stream = stream_key.get()
        photo_keys = stream.photos
        stream.views = stream.views + 1
        photo_urls = []
        for key in photo_keys:
            photo_urls.append(images.get_serving_url(key))

        upload_url = blobstore.create_upload_url('/upload_photo')

        template_values = {
            'stream_name':stream_name,
            'photo_urls':photo_urls,
            'upload_url':upload_url
        }

        template = JINJA_ENVIRONMENT.get_template('ViewSingleStreamPage.html')
        self.response.write(template.render(template_values))
