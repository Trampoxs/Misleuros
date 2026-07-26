import urllib.request
import re

years = [2004, 2005, 2007, 2009, 2012, 2014, 2015, 2016, 2017, 2022, 2023, 2024]
req_headers = {'User-Agent': 'Mozilla/5.0'}

for yr in years:
    url = f"https://www.ecb.europa.eu/euro/coins/comm/html/comm_{yr}.es.html"
    try:
        req = urllib.request.Request(url, headers=req_headers)
        html = urllib.request.urlopen(req).read().decode('utf-8')
        # Find all image links or src attributes
        imgs = re.findall(r'src="([^"]+)"', html)
        es_imgs = [i for i in imgs if 'es' in i.lower() or 'spain' in i.lower() or 'espa' in i.lower()]
        print(f"Year {yr} ES images: {es_imgs}")
    except Exception as e:
        print(f"Error {yr}: {e}")

