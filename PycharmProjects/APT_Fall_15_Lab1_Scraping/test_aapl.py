import json
import yahoo_options_data

computedJson = yahoo_options_data.contractAsJson("aapl.dat")
expectedJson = open("aapl.json").read()
expectedJson_change = open("aapl_change.json").read()
myJson = json.loads(computedJson)
testJson = json.loads(expectedJson_change)
for i in range(0,len(testJson['optionQuotes'])) :
  if myJson['optionQuotes'][i] != testJson['optionQuotes'][i] :
    print 'myJson: ', myJson['optionQuotes'][i]
    print 'testJson: ', testJson['optionQuotes'][i]
for i in range(0,len(testJson['dateUrls'])) :
  if myJson['dateUrls'][i] != testJson['dateUrls'][i] :
    print 'myJson: ', myJson['dateUrls'][i]
    print 'testJson: ', testJson['dateUrls'][i]

if json.loads(computedJson) != json.loads(expectedJson_change):
  print "Test failed!"
  #print "Expected output:", expectedJson_change
  #print "Your output:", computedJson
  #print len(json.loads(expectedJson_change)['optionQuotes'])
  assert False
else:
  print "Test passed"
