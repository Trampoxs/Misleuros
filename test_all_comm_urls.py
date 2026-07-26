import urllib.request

comm_urls = [
    # ES
    ("ES 2005 Quijote", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2005/comm_2005_sp.jpg"),
    ("ES 2010 Cordoba", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_es.jpg"),
    ("ES 2011 Granada", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2011/comm_2011_es.jpg"),
    ("ES 2012 Burgos", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/comm_2012_es.jpg"),
    ("ES 2013 Escorial", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2013/comm_2013_es.jpg"),
    ("ES 2014 Park Guell", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2014/comm_2014_Spain.jpg"),
    ("ES 2014 Felipe VI", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2014/comm_2014_Spain_Head.jpg"),
    ("ES 2015 Altamira", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/comm_2015_Spain.jpg"),
    ("ES 2016 Segovia", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2016/comm_2016_spain.jpg"),
    ("ES 2017 Naranco", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2017/comm_2017_spain_asturias.jpg"),
    ("ES 2018 Santiago", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_spain_santiago.jpg"),
    ("ES 2018 Felipe 50", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_spain_felipe.jpg"),
    ("ES 2019 Avila", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_es_Avila.jpg"),
    ("ES 2020 Mudejar", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_es_unesco_aragon.jpg"),
    ("ES 2021 Toledo", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_es_unesco_toledo.jpg"),
    ("ES 2022 Erasmus", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/Spain.jpg"),

    # DE
    ("DE 2006 Holstentor", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2006/comm_2006_de.jpg"),
    ("DE 2008 St Michaelis", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2008/comm_2008_de.jpg"),
    ("DE 2018 Berlin", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_Germany_berlin.jpg"),
    ("DE 2018 Helmut Schmidt", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_germany_anniversary.jpg"),
    ("DE 2019 Muro Berlin", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_de_30anniv_fallBerlinwall.jpg"),
    ("DE 2019 Bundesrat", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_de_70anniv_Bundesrat.jpg"),
    ("DE 2020 Brandenburg", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_de_brandenburg.jpg"),
    ("DE 2020 Willy Brandt", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_de_50_kniefall_warschau.jpg"),
    ("DE 2022 Thuringen", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/DE-thueringen.jpg"),
    ("DE 2023 Hamburg", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2023/2023_comm_Germany-Hamburg_540x520.jpg"),

    # FR
    ("FR 2010 De Gaulle", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_fr.jpg"),
    ("FR 2018 Bleuet", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_france_cornflower.jpg"),
    ("FR 2019 Asterix", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_fr_60annivAsterix.jpg"),
    ("FR 2020 De Gaulle", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_fr_charles_de_gaulle.jpg"),
    ("FR 2020 Medical", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_fr_medical_research.jpg"),
    ("FR 2021 Unicef", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_fr_unicef.jpg"),
    ("FR 2022 Chirac", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/FR-chirac.jpg"),
    ("FR 2024 JJOO", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2024/2024_comm_France1.JPG"),

    # IT
    ("IT 2004 PMA", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2004/comm_2004_it.jpg"),
    ("IT 2006 Torino", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2006/comm_2006_it.jpg"),
    ("IT 2018 Const", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_italy_anniversary.jpg"),
    ("IT 2018 Health", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2018/comm_2018_italy_health.jpg"),
    ("IT 2019 Da Vinci", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_500anniv_Leodavinci.jpg"),
    ("IT 2020 Montessori", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_it_150mariamontessori.jpg"),
    ("IT 2020 Bomberos", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_it_80annivFoundNatFiredept.jpg"),
    ("IT 2021 Rome Capital", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_it_150_rome_capital.jpg"),
    ("IT 2021 Grazie", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_it_grazie.jpg"),
    ("IT 2022 Borsellino", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/IT-borsellino.jpg"),
    ("IT 2022 Polizia", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/2022_1comm_Italy-polizia_540x540.jpg"),
    
    # Microstates
    ("MC 2021 Boda", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/2021_comm_Monaco1-mariageprincier_540x540.jpg"),
    ("VA 2020 Raphael", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_vc_500raphael_sanzio.jpg"),
    ("VA 2023 Manzoni", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2023/Vatican_Alessandro_Manzoni1.jpg"),
    ("SM 2020 Pope JPII", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_sm_popejpii.jpg"),
]

req_headers = {'User-Agent': 'Mozilla/5.0'}
for label, url in comm_urls:
    try:
        req = urllib.request.Request(url, headers=req_headers)
        res = urllib.request.urlopen(req)
        print(f"[OK 200] {label}")
    except Exception as e:
        print(f"[FAIL {e}] {label}: {url}")

