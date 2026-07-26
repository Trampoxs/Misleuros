import urllib.request

joint_tests = [
    ("2007 DE", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2007/joint_comm_2007_Germany.jpg"),
    ("2007 FR", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2007/joint_comm_2007_France.jpg"),
    ("2007 IT", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2007/joint_comm_2007_Italy.jpg"),
    ("2009 DE", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2009/joint_comm_2009_Germany.jpg"),
    ("2009 FR", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2009/joint_comm_2009_France.jpg"),
    ("2009 IT", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2009/joint_comm_2009_Italy.jpg"),
    ("2012 DE", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/joint_comm_2012_Germany.jpg"),
    ("2012 FR", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/joint_comm_2012_France.jpg"),
    ("2012 IT", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/joint_comm_2012_Italy.jpg"),
    ("2015 DE", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/joint_comm_2015_Germany.jpg"),
    ("2015 FR", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/joint_comm_2015_France.jpg"),
    ("2015 IT", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/joint_comm_2015_Italy.jpg"),
]

req_headers = {'User-Agent': 'Mozilla/5.0'}
for label, url in joint_tests:
    try:
        req = urllib.request.Request(url, headers=req_headers)
        res = urllib.request.urlopen(req)
        print(f"[OK 200] {label}")
    except Exception as e:
        print(f"[FAIL {e}] {label}: {url}")

