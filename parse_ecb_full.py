import urllib.request
import re

years = [2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024]
req_headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'}

for yr in years:
    url = f"https://www.ecb.europa.eu/euro/coins/comm/html/comm_{yr}.en.html"
    try:
        req = urllib.request.Request(url, headers=req_headers)
        html = urllib.request.urlopen(req).read().decode('utf-8')
        
        # Extract blocks or img tags
        imgs = re.findall(rf'src="(comm_{yr}/[^"]+)"', html)
        print(f"=== {yr} ({len(imgs)} imgs) ===")
        for img in imgs:
            full_url = f"https://www.ecb.europa.eu/euro/coins/comm/html/{img}"
            print(f"  {img} -> {full_url}")
    except Exception as e:
        print(f"Err {yr}: {e}")

