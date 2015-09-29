__author__ = 'kenlee'

import webapp2
import jinja2

class ErrorHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        template = JINJA_ENVIRONMENT.get_template('ErrorPage.html')
        self.response.write(template.render())