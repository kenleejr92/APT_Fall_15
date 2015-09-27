__author__ = 'kenlee'
import webapp2
import jinja2
import cgi
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from handlers.Stream import Stream

#/create_stream
class CreateStreamHandler(webapp2.RequestHandler):
    def post(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        # #Get the stream's name from the form
        stream_name = cgi.escape(self.request.get('stream_name'))

        # #Create a new stream with name, no photos, and no views initially
        # #Add stream to the datastore
        new_stream = Stream(name=stream_name,photos=[],views=0)
        new_stream.key = ndb.Key(Stream, stream_name)
        new_stream.put()

        #after upload return to /upload_photo
        upload_url = blobstore.create_upload_url('/upload_photo')

        template_values = {
            'upload_url':upload_url,
            'stream_name':stream_name
        }

        template = JINJA_ENVIRONMENT.get_template('CreateStreamPage.html')
        self.response.write(template.render(template_values))