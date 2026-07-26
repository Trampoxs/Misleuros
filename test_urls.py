import urllib.request

urls = [
    # Joint issues on ECB
    ("2007 Tratado Roma", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2007/comm_2007_es.jpg"),
    ("2009 UEM", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2009/comm_2009_es.jpg"),
    ("2012 10 Y Euro", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/comm_2012_es.jpg"),
    ("2015 Bandera", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/comm_2015_es.jpg"),
    ("2022 Erasmus", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/Spain.jpg"),
    
    # Spanish commemoratives on ECB
    ("ES 2005 Quijote", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2005/comm_2005_es.jpg"),
    ("ES 2010 Cordoba", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_es.jpg"),
    ("ES 2011 Granada", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2011/comm_2011_es.jpg"),
    ("ES 2012 Burgos", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/comm_2012_es_burgos.jpg"),
    ("ES 2013 Escorial", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2013/comm_2013_es.jpg"),
    ("ES 2014 Park Guell", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2014/comm_2014_es_parkguell.jpg"),
    ("ES 2014 Felipe VI", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2014/comm_2014_es_felipe.jpg"),
    ("ES 2015 Altamira", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/comm_2015_es_altamira.jpg"),
    ("ES 2016 Segovia", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2016/comm_2016_es_segovia.jpg"),
    ("ES 2017 Naranco", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2017/comm_2017_es_asturias.jpg"),
    ("ES 2018 Santiago", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_es_santiago.jpg"),
    ("ES 2018 Felipe VI 50", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_es_felipe.jpg"),
    ("ES 2019 Avila", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_es_Avila.jpg"),
    ("ES 2020 Mudejar", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_es_unesco_aragon.jpg"),
    ("ES 2021 Toledo", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_es_unesco_toledo.jpg"),
    ("ES 2022 Garajonay", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/comm_2022_es_garajonay.jpg"),
]

req_headers = {'User-Agent': 'Mozilla/5.0'}
for label, url in urls:
    try:
        req = urllib.request.Request(url, headers=req_headers)
        res = urllib.request.urlopen(req)
        print(f"[OK 200] {label}: {url}")
    except Exception as e:
        print(f"[FAIL {e}] {label}: {url}")

