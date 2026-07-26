import urllib.request
import re

years = [2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024]
req_headers = {'User-Agent': 'Mozilla/5.0'}

for yr in years:
    url = f"https://www.ecb.europa.eu/euro/coins/comm/html/comm_{yr}.en.html"
    try:
        req = urllib.request.Request(url, headers=req_headers)
        html = urllib.request.urlopen(req).read().decode('utf-8')
        matches = re.findall(rf'([a-zA-Z0-9_\-%\.]+\.(?:jpg|JPG|png|PNG))', html)
        comm_imgs = [m for m in set(matches) if 'logo' not in m.lower() and 'header' not in m.lower() and 'icon' not in m.lower() and 'social' not in m.lower()]
        print(f"Year {yr}: {comm_imgs}")
    except Exception as e:
        print(f"Error {yr}: {e}")

