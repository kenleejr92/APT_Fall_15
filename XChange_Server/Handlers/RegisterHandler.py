__author__ = 'kenlee'
import webapp2
import cgi
import json
from google.appengine.ext import ndb
from Handlers.UserData import User
from google.appengine.api import images
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers

class RegisterHandler(blobstore_handlers.BlobstoreUploadHandler):
    def get(self):
        upload_url = blobstore.create_upload_url('/register_user')
        upload_url_dict = {'upload_url':upload_url}
        upload_url_json = json.dumps(upload_url_dict,indent=4, separators=(',', ': '))
        self.response.write(upload_url_json)
    def post(self):
        test = {'test':'Hello'}
        test_json = json.dumps(test,indent=4, separators=(',', ': '))
        self.response.write(test_json)
        upload = self.get_uploads('file')[0]
        user_name = cgi.escape(self.request.get('user_name'))
        user_id = cgi.escape(self.request.get('user_id'))
        user_phone_number = cgi.escape(self.request.get('phone_number'))
        user_email = cgi.escape(self.request.get('email'))
        blob_key = upload.key()
        url = images.get_serving_url(blob_key)
        user = User(name=user_name, id=user_id,phone_number=user_phone_number,email=user_email,bc_url=url)
        user.key = ndb.Key(User, user_id)
        user.put()
