import urllib.request

comm_list = [
    # Joint issues (ECB / Wikimedia)
    ("EU_2007", "https://upload.wikimedia.org/wikipedia/commons/d/d4/2_Euro_2007_Treaty_of_Rome.jpg"),
    ("EU_2009", "https://upload.wikimedia.org/wikipedia/commons/1/1a/2_Euro_2009_EMU.jpg"),
    ("EU_2012", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2012/comm_2012_es.jpg"),
    ("EU_2015", "https://upload.wikimedia.org/wikipedia/commons/5/52/2_Euro_2015_Flag_EU.jpg"),
    ("EU_2022", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/Spain.jpg"),

    # Spain
    ("ES_2005", "https://upload.wikimedia.org/wikipedia/commons/0/07/2_euro_commemorative_coin_Spain_2005.jpg"),
    ("ES_2010", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_es.jpg"),
    ("ES_2011", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2011/comm_2011_es.jpg"),
    ("ES_2012", "https://upload.wikimedia.org/wikipedia/commons/0/0d/Burgos_Cathedral_2_euro_coin.jpg"),
    ("ES_2013", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2013/comm_2013_es.jpg"),
    ("ES_2014_Gau", "https://upload.wikimedia.org/wikipedia/commons/b/b5/2_Euro_Spain_2014_Park_Guell.jpg"),
    ("ES_2014_Fel", "https://upload.wikimedia.org/wikipedia/commons/5/5e/2_Euro_Spain_2014_Felipe_VI.jpg"),
    ("ES_2015_Alt", "https://upload.wikimedia.org/wikipedia/commons/3/3a/2_Euro_Spain_2015_Altamira.jpg"),
    ("ES_2016_Seg", "https://upload.wikimedia.org/wikipedia/commons/2/23/2_Euro_Spain_2016_Segovia.jpg"),
    ("ES_2017_Nar", "https://upload.wikimedia.org/wikipedia/commons/9/9f/2_Euro_Spain_2017_Asturias.jpg"),
    ("ES_2018_San", "https://upload.wikimedia.org/wikipedia/commons/d/d3/2_Euro_Spain_2018_Santiago.jpg"),
    ("ES_2018_Fel", "https://upload.wikimedia.org/wikipedia/commons/7/7b/2_Euro_Spain_2018_Felipe_50.jpg"),
    ("ES_2019_Avi", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_es_Avila.jpg"),
    ("ES_2020_Mud", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_es_unesco_aragon.jpg"),
    ("ES_2021_Tol", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/comm_2021_es_unesco_toledo.jpg"),
    ("ES_2022_Gar", "https://upload.wikimedia.org/wikipedia/commons/8/87/2_Euro_Spain_2022_Garajonay.jpg"),
    ("ES_2022_Elc", "https://upload.wikimedia.org/wikipedia/commons/b/b2/2_Euro_Spain_2022_Elcano.jpg"),
    ("ES_2023_Cac", "https://upload.wikimedia.org/wikipedia/commons/1/1a/2_Euro_Spain_2023_Caceres.jpg"),
    ("ES_2023_Pre", "https://upload.wikimedia.org/wikipedia/commons/0/03/2_Euro_Spain_2023_EU_Presidency.jpg"),
    ("ES_2024_Sev", "https://upload.wikimedia.org/wikipedia/commons/f/f9/2_Euro_Spain_2024_Sevilla.jpg"),
    ("ES_2024_Pol", "https://upload.wikimedia.org/wikipedia/commons/6/61/2_Euro_Spain_2024_Policia.jpg"),

    # Germany
    ("DE_2006", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2006/comm_2006_de.jpg"),
    ("DE_2007", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2007/comm_2007_de.jpg"),
    ("DE_2008", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2008/comm_2008_de.jpg"),
    ("DE_2015_Reu", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/comm_2015_de_reunification.jpg"),
    ("DE_2019_Mur", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_de_30anniv_fallberlinwall.jpg"),
    ("DE_2020_Bra", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_de_brandenburg.jpg"),
    ("DE_2022_Thu", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/DE-thueringen.jpg"),
    ("DE_2023_Ham", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2023/2023_comm_Germany-Hamburg_540x520.jpg"),

    # France
    ("FR_2010", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2010/comm_2010_fr.jpg"),
    ("FR_2016", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2016/comm_2016_fr_euro2016.jpg"),
    ("FR_2019", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_fr_60anniv_asterix.jpg"),
    ("FR_2021", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2021/2021_comm_France1-olympics%20540x540.jpg"),
    ("FR_2022", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/FR-chirac.jpg"),

    # Italy
    ("IT_2004", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2004/comm_2004_it.jpg"),
    ("IT_2006", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2006/comm_2006_it.jpg"),
    ("IT_2015", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2015/comm_2015_it_dante.jpg"),
    ("IT_2019", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2019/comm_2019_500anniv_Leodavinci.jpg"),
    ("IT_2020", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2020/comm_2020_it_150mariamontessori.jpg"),
    ("IT_2022", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2022/IT-borsellino.jpg"),

    # Monaco, San Marino, Vatican
    ("MC_2007", "https://upload.wikimedia.org/wikipedia/en/9/90/2_euro_Grace_Kelly_Monaco_2007.jpg"),
    ("VA_2005", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2005/comm_2005_va.jpg"),
    ("VA_2023", "https://www.ecb.europa.eu/euro/coins/comm/html/comm_2023/Vatican_Alessandro_Manzoni1.jpg"),
]

req_headers = {'User-Agent': 'Mozilla/5.0'}
for key, url in comm_list:
    try:
        req = urllib.request.Request(url, headers=req_headers)
        res = urllib.request.urlopen(req)
        print(f"[OK 200] {key}")
    except Exception as e:
        print(f"[FAIL {e}] {key}: {url}")

