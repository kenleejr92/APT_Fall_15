__author__ = 'kenlee'

from google.appengine.ext import ndb

class Stream(ndb.Model):
    name = ndb.StringProperty()
    photos = ndb.BlobKeyProperty(repeated=True)
    views = ndb.IntegerProperty()