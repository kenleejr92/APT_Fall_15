import json
import sys
import re
import urllib
from bs4 import BeautifulSoup


def contractAsJson(filename):
  #Current Price
  jsonQuoteData = {}
  fhand = open(filename,"r")
  soup = BeautifulSoup(fhand, 'html.parser')
  h2Tags = soup.findAll('h2')
  #Find the company appreviation
  for h2 in h2Tags :
    if re.match('.*\((.+)\).*',h2.string) :
      comp = re.findall('.*\((.+)\).*',h2.string)
      break
  company = comp[0].lower()
  #Find the price by appending the company name to an id string
  company_id = "yfs_l84_" + company
  priceTag = soup.findAll(id=company_id)
  price = priceTag[0].string
  jsonQuoteData["currPrice"]=float(price)


  #Expiry URLs
  table = soup.find(id="yfncsumtab") #get tag of table
  tableCells = table.findAll("td") #tags of table cells
  #find the tag that has the string corresponding to expirations
  for tc in tableCells :
    if re.match('.*View By Expiration:.*',tc.text) :
      exp_date_list = tc
      break
  #extract the links
  exp_links= exp_date_list.findAll('a')
  exp_urls = []
  for a in exp_links :
    if re.match('.*m=20[0-9][0-9]-[0|1][0-9].*',a['href']) :
      exp_urls.append("http://finance.yahoo.com"+a['href'].replace("&","&amp;"))
  jsonQuoteData["dateUrls"]= exp_urls


  #Call Options
  optionQuotes = []
  #find the table of options
  bTitles = exp_date_list.findAll('b')
  for b in bTitles :
    if re.match("^Call Options$",b.text) :
      callOptions = b
  for parent in callOptions.parents:
    if parent.name == 'table':
      titleTable = parent
      break
  dataTable = titleTable.next_sibling
  dataTable = dataTable.findAll('table')
  keys = []
  #Iterate through table rows
  for tr in dataTable[0].findAll('tr') :
    oQ={}
    if re.match('.*Strike.*',tr.text) :
      #Assemble the key value list in order of discovery
      for th in tr.findAll('th') :
        if th.text == 'Chg':
          keys.append('Change')
        elif th.text == 'Open Int' :
          keys.append('Open')
        else :
          keys.append(th.text)
      continue
    else :
      i = 0
      #Iterate through table columns
      for td in tr.findAll('td') :
        if keys[i] == 'Symbol' :
           #Extract symbol, date, and time
           symbolDateType = re.findall('^[a-zA-Z]+[0-9]+[a-zA-Z]', td.text)[0]
           type = re.findall('.*([a-zA-Z])$',symbolDateType)[0]
           date = re.findall('.*([0-9]{6})[a-zA-Z]$',symbolDateType)[0]
           symbol = re.findall('(.*)[0-9]{6}[a-zA-Z]$',symbolDateType)[0]
           oQ['Symbol']= symbol
           oQ['Date'] = date
           oQ['Type'] = type
        elif keys[i] == 'Change':
          #Use class id to determine positive or negative change
          img = td.findAll('img')
          if len(img) > 0 :
            if img[0]['class'][0] == 'pos_arrow' :
              oQ[keys[i]] = '+' + re.findall('.*([0-9]+\.[0-9]*)', td.text)[0]
            elif img[0]['class'][0] == 'neg_arrow' :
              oQ[keys[i]] = "-" + re.findall('.*([0-9]+\.[0-9]*)', td.text)[0]
          else :
            oQ[keys[i]] = td.text
        else :
          oQ[keys[i]] = td.text
        i = i+1
    optionQuotes.append(oQ)



  #Put Options
  #Perform the same as above for Put Options
  bTitles = exp_date_list.findAll('b')
  for b in bTitles :
    if re.match("^Put Options$",b.text) :
      callOptions = b
  for parent in callOptions.parents:
    if parent.name == 'table':
      titleTable = parent
      break
  dataTable = titleTable.next_sibling
  dataTable = dataTable.findAll('table')
  keys = []
  for tr in dataTable[0].findAll('tr') :
    oQ={}
    if re.match('.*Strike.*',tr.text) :
      for th in tr.findAll('th') :
        if th.text == 'Chg':
          keys.append('Change')
        elif th.text == 'Open Int' :
          keys.append('Open')
        else :
          keys.append(th.text)
      continue
    else :
      i = 0
      for td in tr.findAll('td') :
        if keys[i] == 'Symbol' :
            symbolDateType = re.findall('^[a-zA-Z]+[0-9]+[a-zA-Z]', td.text)[0]
            type = re.findall('.*([a-zA-Z])$',symbolDateType)[0]
            date = re.findall('.*([0-9]{6})[a-zA-Z]$',symbolDateType)[0]
            symbol = re.findall('(.*)[0-9]{6}[a-zA-Z]$',symbolDateType)[0]
            oQ['Symbol']= symbol
            oQ['Date'] = date
            oQ['Type'] = type
        elif keys[i] == 'Change':
          img = td.findAll('img')
          if len(img) > 0 :
            if img[0]['class'][0] == 'pos_arrow' :
              oQ[keys[i]] = '+' + re.findall('.*([0-9]+\.[0-9]*)', td.text)[0]
            elif img[0]['class'][0] == 'neg_arrow' :
              oQ[keys[i]] = "-" + re.findall('.*([0-9]+\.[0-9]*)', td.text)[0]
          else :
            oQ[keys[i]] = td.text
        else :
          oQ[keys[i]] = td.text
        i = i+1
    optionQuotes.append(oQ)


  def deleteComma(x) :
    interest = x['Open']
    return int(interest.replace(",",""))

  #Sort by open interest, and break ties with order of appearance on page
  def sortingFunc(x,y) :
    if deleteComma(x) != deleteComma(y) :
      return cmp(deleteComma(x),deleteComma(y))
    elif x['Type'] != y['Type'] :
      return  not cmp(x['Type'],y['Type'])
    else :
      return not cmp(float(x['Strike']),float(y['Strike']))

  optionQuotes.sort(lambda x,y: sortingFunc(x,y), reverse=True)
  jsonQuoteData['optionQuotes'] = optionQuotes
  return json.dumps(jsonQuoteData, sort_keys=True, indent=4, separators=(',', ': ') )

