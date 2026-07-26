import urllib.request

urls = [
    ("2005 Don Quijote", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2005/comm_2005_es.jpg"),
    ("2007 Roma ES", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2007/joint_comm_2007_Spain.jpg"),
    ("2009 UEM ES", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2009/joint_comm_2009_Spain.jpg"),
    ("2010 Cordoba", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_es.jpg"),
    ("2011 Granada", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2011/comm_2011_es.jpg"),
    ("2012 Burgos", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/comm_2012_es.jpg"),
    ("2012 10Y Euro ES", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/joint_comm_2012_Spain.jpg"),
    ("2013 Escorial", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2013/comm_2013_es.jpg"),
    ("2014 Park Guell", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2014/comm_2014_Spain.jpg"),
    ("2014 Felipe VI", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2014/comm_2014_Spain_Head.jpg"),
    ("2015 Altamira", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/comm_2015_Spain.jpg"),
    ("2015 Bandera ES", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/joint_comm_2015_Spain.jpg"),
    ("2016 Segovia", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2016/comm_2016_spain.jpg"),
    ("2017 Naranco", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2017/comm_2017_spain_asturias.jpg"),
    ("2018 Santiago", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_spain_santiago.jpg"),
    ("2018 Felipe 50", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_spain_felipe.jpg"),
    ("2019 Avila", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_es_Avila.jpg"),
    ("2020 Mudejar", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_es_unesco_aragon.jpg"),
    ("2021 Toledo", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_es_unesco_toledo.jpg"),
    ("2022 Erasmus ES", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/Spain.jpg"),
]

req_headers = {'User-Agent': 'Mozilla/5.0'}
for label, url in urls:
    try:
        req = urllib.request.Request(url, headers=req_headers)
        res = urllib.request.urlopen(req)
        print(f"[OK 200] {label}")
    except Exception as e:
        print(f"[FAIL {e}] {label}: {url}")

