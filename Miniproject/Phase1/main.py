import webapp2
import cgi

from google.appengine.api import users
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.ext.webapp.util import run_wsgi_app

# A custom datastore model for associating users with uploaded files.

class Stream(ndb.Model):
    name = ndb.StringProperty()
    photos = ndb.BlobKeyProperty(repeated=True)
    views = ndb.IntegerProperty()
    user = ndb.StringProperty()

class MainPageHandler(webapp2.RequestHandler):
    def get(self):
        # Checks for active Google account session
        user = users.get_current_user()

        if user:
            self.response.headers['Content-Type'] = 'text/html; charset=utf-8'
            self.response.write('Connexus user: ' + user.nickname())
        else:
            self.redirect(users.create_login_url(self.request.uri))


        #self.response.out.write('</ul>')
        #self.response.out.write('<form action="/" method="logged_out" >')
        #self.response.out.write('''<br><input type="logout" name="logout" value="Logout"> </form></body></html>''')


        self.response.out.write(''
                                '<form align="right" name="form1" method="get" action="/logout">'
                                    '<label class="logoutLblPos">'
                                    '<input name="logged_out" type="submit" id="submit2" value="log out">'
                                    '</label>'
                                '</form>')

        self.response.out.write('<html><body>')
        #Get the list of streams
        qry = Stream.query(Stream.user == user.nickname())
        #Print the names in a list
        self.response.out.write('<ul>')
        for stream in qry :
            self.response.out.write('<li><a href="/view_stream/?stream_name=%s" >%s</a></li>' % (stream.name, stream.name))
        self.response.out.write('</ul>')
        self.response.out.write('<form action="/create_stream" method="post" >')
        self.response.out.write('''Create Stream: <input type="text" name="stream_name" value="Name"><br><input type="submit"
            name="submit" value="Submit"> </form></body></html>''')




class LoginPageHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write("You have logged out")
        #self.redirect(users.create_logout_url('/'))
        #user = users.get_current_user()
        #greeting = ('Welcome, %s! (<a href="%s">sign out</a>)' %
                        #(user.nickname(), users.create_logout_url('/')))
        #users.create_logout_url('/')
        #users.get_current_user().

#/view_stream
class ViewStreamHandler(blobstore_handlers.BlobstoreDownloadHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'image/png'
        stream_name = cgi.escape(self.request.get('stream_name'))
        stream_key = ndb.Key(Stream,stream_name)
        stream = stream_key.get()
        for photo in stream.photos:
            if not blobstore.get(photo):
                self.error(404)
            else:
                self.send_blob(photo)
        # upload_url = blobstore.create_upload_url('/upload_photo')
        # # The method must be "POST" and enctype must be set to "multipart/form-data".
        # self.response.out.write('<html><body>')
        # self.response.out.write('<form action="%s" method="POST" enctype="multipart/form-data">' % upload_url )
        # self.response.out.write('''Upload File: <input type="file" name="file"><br> <input type="submit"
        #     name="submit" value="Submit to %s"> </form></body></html>''' % stream_name)

#/create_stream
class CreateStreamHandler(webapp2.RequestHandler):
    def post(self):
        # #Get the stream's name from the form
        stream_name = cgi.escape(self.request.get('stream_name'))
        #
        # #Create a new stream with name, no photos, and no views initially
        # #Add stream to the datastore
        new_stream = Stream(name=stream_name,photos=[],views=0, user=users.get_current_user().nickname())
        new_stream.key = ndb.Key(Stream, stream_name)
        new_stream.put()

        upload_url = blobstore.create_upload_url('/upload_photo')
        # The method must be "POST" and enctype must be set to "multipart/form-data".
        self.response.out.write('<html><body>')
        self.response.out.write('<form action="%s" method="POST" enctype="multipart/form-data">' % upload_url )
        self.response.out.write('''Upload File: <input type="file" name="file"><br> <input type="submit"
            name="submit" value="Submit to %s"> </form></body></html>''' % stream_name)

#/UploadPhoto
class PhotoUploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        try:
            upload = self.get_uploads()[0]
            str = cgi.escape(self.request.get('submit'))
            stream_name = str.split()[2]
            blob_key = upload.key()
            stream_key = ndb.Key(Stream,stream_name)
            stream = stream_key.get()
            stream.photos.append(blob_key)
            stream.put()

            self.redirect('/view_photo/%s' % upload.key())

        except:
            self.error(500)

#/view_photo
class ViewPhotoHandler(blobstore_handlers.BlobstoreDownloadHandler):
    def get(self, photo_key):
        if not blobstore.get(photo_key):
            self.error(404)
        else:
            self.send_blob(photo_key)


app = webapp2.WSGIApplication([('/', MainPageHandler),
                               ('/logout', LoginPageHandler),
                               ('/create_stream', CreateStreamHandler),
                               ('/upload_photo', PhotoUploadHandler),
                               ('/view_photo/([^/]+)?', ViewPhotoHandler),
                               ('/view_stream/.*', ViewStreamHandler)
                              ], debug=True)