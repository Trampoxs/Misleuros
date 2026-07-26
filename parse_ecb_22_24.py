import urllib.request
import re

years = [2022, 2023, 2024]
req_headers = {'User-Agent': 'Mozilla/5.0'}

for yr in years:
    url = f"https://www.ecb.europa.eu/euro/coins/comm/html/comm_{yr}.es.html"
    try:
        req = urllib.request.Request(url, headers=req_headers)
        html = urllib.request.urlopen(req).read().decode('utf-8')
        # Find images in HTML
        imgs = re.findall(r'src="(comm_[^"]+)"', html)
        print(f"=== {yr} ===")
        for img in imgs:
            print(f"  {img}")
    except Exception as e:
        print(f"Err {yr}: {e}")

