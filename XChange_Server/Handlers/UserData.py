__author__ = 'kenlee'

from google.appengine.ext import ndb

class User(ndb.Model):
  name = ndb.StringProperty()
  id = ndb.StringProperty()
  phone_number = ndb.StringProperty()
  email = ndb.StringProperty()
