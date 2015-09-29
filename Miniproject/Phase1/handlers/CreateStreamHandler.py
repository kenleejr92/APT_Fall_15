__author__ = 'kenlee'
import webapp2
import jinja2
import cgi
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.api import users
from handlers.Stream import Stream

#/create_stream
class CreateStreamHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        template = JINJA_ENVIRONMENT.get_template('CreateStreamPage.html')
        self.response.write(template.render())

    def post(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        # #Get the stream's name from the form
        stream_name = cgi.escape(self.request.get('stream_name'))

        #stream already exists
        stream = Stream.query(Stream.name == stream_name).get()
        if stream:
            self.redirect('/error')
        else :
            #Create a new stream with name, no photos, and no views initially
            #Add stream to the datastore
            user = users.get_current_user()
            new_stream = Stream(owner_id = user.user_id(),name=stream_name,photos=[],views=0, subscribed_users=[], view_queue=[])
            new_stream.key = ndb.Key(Stream, stream_name)
            new_stream.put()
            self.redirect('/management')
