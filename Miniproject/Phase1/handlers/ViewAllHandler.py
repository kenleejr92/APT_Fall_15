__author__ = 'kenlee'

import webapp2
import json
import os
import cgi
import jinja2
import datetime
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api import images
from google.appengine.api import users
from Stream import Stream

class ViewAllHandler(webapp2.RequestHandler):
    def setup(self, currentTab):
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
        }

        template = JINJA_ENVIRONMENT.get_template('Welcome.html')
        self.response.write(template.render(userInfo))

        #Get the list of streams
        streams = Stream.query()

        streamNames = []
        for stream in streams:
            streamNames.append(str(stream.name))

        template_values = {
            'streams':streams
        }

        template = JINJA_ENVIRONMENT.get_template('Search.html')
        self.response.write(template.render(streams = json.dumps(streamNames)))

        #test search header
        searchHead = JINJA_ENVIRONMENT.get_template('Header.html')
        self.response.write(searchHead.render(current = currentTab))

    def get(self):
        self.setup('view')

        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()
        user_id = user.user_id()

        stream_name = 'Cali'
        stream_key = ndb.Key(Stream,stream_name)
        stream = stream_key.get()


        # if stream.owner_id == user.user_id() :
        #     owner = True
        # else :
        #     owner = False
        #     stream.views = stream.views + 1
        #     stream.view_queue.append(datetime.datetime.now())
        #     stream.put()

        photo_keys = stream.photos
        photo_urls = []
        for key in photo_keys:
            photo_urls.append(images.get_serving_url(key))


        upload_url = blobstore.create_upload_url('/upload_photo/?stream_name=%s' % stream_name)

        #Get the list of streams
        my_streams = Stream.query(Stream.owner_id == user_id).order(Stream.timestamp)
        template_values = {
            'my_streams':my_streams,
            'photo_urls': photo_urls
        }

        # template = JINJA_ENVIRONMENT.get_template('ViewAllStreamsPage.html')
        # self.response.write(template.render(template_values))

        template = JINJA_ENVIRONMENT.get_template('PhotoAlbumStreams.html')
        self.response.write(template.render(template_values))