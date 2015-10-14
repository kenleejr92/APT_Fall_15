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
import json



def random_date(start, end):
    return start + timedelta(seconds=randint(0, int((end - start).total_seconds())))

#/view_stream/stream_name
class ViewStreamHandler(blobstore_handlers.BlobstoreDownloadHandler):
    # input_values = {}

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
            'logout_url': logout_url
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

        photo_keys = stream.photos



        photo_objs = []
        for key in photo_keys:
            photo_objs.append({'url': images.get_serving_url(key),
                               'lat': -25.363,
                               'long': 131.044,
                               'date': random_date(datetime.date(2015,12,1),datetime.date(2015,12,25))
                               })

        less_objs = []
        for x in range(0,4):
            if(len(photo_objs)>0):less_objs.append(photo_objs.pop())


        upload_url = blobstore.create_upload_url('/upload_photo/?stream_name=%s' % stream_name)


        template_values = {
            'owner':owner,
            'stream_name':stream_name,
            'photo_objs':less_objs,
            'upload_url':upload_url
        }

        template = JINJA_ENVIRONMENT.get_template('ViewSingleStreamPage.html')
        self.response.write(template.render(template_values))

    def post(self):
        self.setup('view')

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

        photo_keys = stream.photos
        photo_objs = []
        for key in photo_keys:
            photo_objs.append({'url': images.get_serving_url(key),
                               'lat': -25.363,
                               'long': 131.044,
                               'date': random_date(datetime.date(2015,12,1),datetime.date(2015,12,25))
                               })


        upload_url = blobstore.create_upload_url('/upload_photo/?stream_name=%s' % stream_name)

        input_values = {
            'owner':owner,
            'stream_name':stream_name,
            'photo_objs':photo_objs,
            'upload_url':upload_url
        }

        template = JINJA_ENVIRONMENT.get_template('ViewSingleStreamPage.html')
        self.response.write(template.render(input_values))
