__author__ = 'kenlee'

import cgi
import jinja2
import datetime
from datetime import timedelta
from random import randint
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api import images
from google.appengine.api import users
from Stream import Stream
from Stream import Photo
import json
from BaseHandler import BaseHandler



def random_date(start, end):
    return start + timedelta(seconds=randint(0, int((end - start).total_seconds())))

#/view_stream/stream_name
class ViewStreamHandler(blobstore_handlers.BlobstoreDownloadHandler, BaseHandler):
    # input_values = {}



    def get(self):
        self.cache('view')

        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()

        stream_name = cgi.escape(self.request.get('stream_name'))
        stream_key = ndb.Key(Stream,stream_name)
        stream = stream_key.get()


        if stream.owner_id == user.user_id() :
            owner = True
        else :
            owner = False
            stream.views = stream.views + 1
            stream.view_queue.append(datetime.datetime.now())
            stream.put()

        photo_objs = stream.photos
        upload_url = blobstore.create_upload_url('/upload_photo/?stream_name=%s' % stream_name)


        template_values = {
            'owner':owner,
            'stream_name':stream_name,
            'photo_objs':photo_objs,
            'upload_url':upload_url
        }

        template = JINJA_ENVIRONMENT.get_template('ViewSingleStreamPage.html')
        self.response.write(template.render(template_values))

    def post(self):
        self.cache('view')

        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()

        stream_name = cgi.escape(self.request.get('stream_name'))
        stream_key = ndb.Key(Stream,stream_name)
        stream = stream_key.get()


        if stream.owner_id == user.user_id() :
            owner = True
        else :
            owner = False
            stream.views = stream.views + 1
            stream.view_queue.append(datetime.datetime.now())
            stream.put()

        photo_objs = stream.photos
        upload_url = blobstore.create_upload_url('/upload_photo/?stream_name=%s' % stream_name)

        input_values = {
            'owner':owner,
            'stream_name':stream_name,
            'photo_objs':photo_objs,
            'upload_url':upload_url
        }

        template = JINJA_ENVIRONMENT.get_template('ViewSingleStreamPage.html')
        self.response.write(template.render(input_values))
