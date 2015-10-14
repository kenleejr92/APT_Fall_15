__author__ = 'kenlee'

import webapp2
import jinja2
import cgi
import json
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.api import users
from handlers.Stream import Stream
from BaseHandler import BaseHandler

#/search_streams
class SearchStreamHandler(webapp2.RequestHandler, BaseHandler):
    def get(self):
        self.cache('search')


    def post(self):
        query_string = cgi.escape(self.request.get('query_string'))
        if not query_string :
                self.errorpage("That query does not exist")
        else:
            if(query_string[0]=='#'):
                #self.response.write("TAGGG")
                queryAll = Stream.query()
                tagToStream = []
                for query in queryAll:
                    tags = query.tags
                    for tag in tags:
                        if(query_string==tag):
                            #self.response.write(tag[0:])
                            tagToStream.append(query)

                # for streamName in tagToStream:
                #     self.response.write(streamName + " ")

                if(len(tagToStream)>1):self.multiple(query_string, tagToStream)
                elif(len(tagToStream)==1):
                    goToStream = "/view_stream/?stream_name=" + str(tagToStream[0].name)
                    self.redirect(goToStream)
                else:
                    self.errorpage("That query does not exist")

            else:
                queryStream = Stream.query(Stream.name == query_string)
                entity = queryStream.get()
                if entity is not None:
                    goToStream = "/view_stream/?stream_name=" + query_string
                    self.redirect(goToStream)
                else:
                    self.errorpage("That query does not exist")

    def multiple(self, tagname, streams):
        self.cache('')

        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        template_values = {
            'query': tagname,
            'my_streams':streams,
        }

        template = JINJA_ENVIRONMENT.get_template('MultipleTagHits.html')
        self.response.write(template.render(template_values))
