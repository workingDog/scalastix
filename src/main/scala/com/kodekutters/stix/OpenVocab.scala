package com.kodekutters.stix

/**
  * STIX-2.1 vocabularies part of STIX-2.1 protocol
  *
  * https://oasis-open.github.io/cti-documentation/
  *
  * Author: R. Wathelet May 2017
  */

sealed trait OpenVocab

object attack_motivation_ov extends OpenVocab {
  val accidental = "accidental"
  val coercion = "coercion"
  val dominance = "dominance"
  val ideology = "ideology"
  val notoriety = "notoriety"
  val revenge = "revenge"
  val unpredictable = "unpredictable"
  val organizational_gain = "organizational-gain"
  val personal_gain = "personal-gain"
  val personal_satisfaction = "personal-satisfaction"
}

object attack_resource_level_ov extends OpenVocab {
  val individual = "individual"
  val club = "club"
  val contest = "contest"
  val team = "team"
  val organization = "organization"
  val government = "government"
}

object identity_class_ov extends OpenVocab {
  val individual = "individual"
  val group = "group"
  val contest = "contest"
  val organization = "organization"
  val `class` = "class"
  val unknown = "unknown"
}

object indicator_label_ov extends OpenVocab {
  val anomalous_activity = "anomalous-activity"
  val anonymization = "anonymization"
  val benign = "benign"
  val organization = "organization"
  val compromised = "compromised"
  val malicious_activity = "malicious-activity"
  val attribution = "attribution"
}

object industry_sector_ov extends OpenVocab {
  val agriculture = "agriculture"
  val aerospace = "aerospace"
  val automotive = "automotive"
  val communications = "communications"
  val construction = "construction"
  val defence = "defence"
  val education = "education"
  val entertainment = "entertainment"
  val government_national = "government-national"
  val government_regional = "government-regional"
  val financial_services = "financial-services"
  val government_local = "government-local"
  val government_public_services = "government-public-services"
  val healthcare = "healthcare"
  val hospitality_leisure = "hospitality-leisure"
  val infrastructure = "infrastructure"
  val insurance = "insurance"
  val manufacturing = "manufacturing"
  val mining = "mining"
  val non_profit = "non-profit"
  val pharmaceuticals = "pharmaceuticals"
  val retail = "retail"
  val technology = "technology"
  val telecommunications = "telecommunications"
  val transportation = "transportation"
  val utilities = "utilities"
}

object malware_label_ov extends OpenVocab {
  val adware = "adware"
  val backdoor = "backdoor"
  val bot = "bot"
  val ddos = "ddos"
  val dropper = "dropper"
  val exploit_kit = "exploit-kit"
  val keylogger = "keylogger"
  val ransomware = "ransomware"
  val remote_access_trojan = "remote-access-trojan"
  val resource_exploitation = "resource-exploitation"
  val rogue_security_software = "rogue-security-software"
  val rootkit = "rootkit"
  val screen_capture = "screen-capture"
  val spyware = "spyware"
  val trojan = "trojan"
  val virus = "virus"
  val worm = "worm"
}

object report_label_ov extends OpenVocab {
  val threat_report = "threat-report"
  val attack_pattern = "attack-pattern"
  val campaign = "campaign"
  val identity = "identity"
  val indicator = "indicator"
  val malware = "malware"
  val observed_data = "observed-data"
  val threat_actor = "threat-actor"
  val tool = "tool"
  val vulnerability = "vulnerability"
}

object threat_actor_label_ov extends OpenVocab {
  val activist = "activist"
  val competitor = "competitor"
  val crime_syndicate = "crime-syndicate"
  val criminal = "criminal"
  val hacker = "hacker"
  val insider_accidental = "insider-accidental"
  val insider_disgruntled = "insider-disgruntled"
  val nation_state = "nation-state"
  val sensationalist = "sensationalist"
  val spy = "spy"
  val terrorist = "terrorist"
}

object threat_actor_role_ov extends OpenVocab {
  val agent = "agent"
  val director = "director"
  val independent = "independent"
  val infrastructure_architect = "infrastructure-architect"
  val infrastructure_operator = "infrastructure-operator"
  val malware_author = "malware-author"
  val sponsor = "sponsor"
}

object threat_actor_sophistication_ov extends OpenVocab {
  val none = "none"
  val minimal = "minimal"
  val intermediate = "intermediate"
  val advanced = "advanced"
  val expert = "expert"
  val innovator = "innovator"
  val strategic = "strategic"
}

object tool_label_ov extends OpenVocab {
  val denial_of_service = "denial-of-service"
  val exploitation = "exploitation"
  val information_gathering = "information-gathering"
  val network_capture = "network-capture"
  val credential_exploitation = "credential-exploitation"
  val remote_access = "remote-access"
  val vulnerability_scanning = "vulnerability-scanning"
}

//---------------------------------------------------------------------------
//-------------------for Observables-----------------------------------------
//---------------------------------------------------------------------------

object hash_algo_ov extends OpenVocab {
  val MD5 = "MD5"
  val MD6 = "MD6"
  val RIPEMD_160 = "RIPEMD-160"
  val SHA_1 = "SHA-1"
  val SHA_224 = "SHA-224"
  val SHA_256 = "SHA-256"
  val SHA_384 = "SHA-384"
  val SHA_512 = "SHA-512"
  val SHA3_224 = "SHA3-224"
  val SHA3_256 = "SHA3-256"
  val SHA3_384 = "SHA3-384"
  val SHA3_512 = "SHA3-512"
  val ssdeep = "ssdeep"
  val WHIRLPOOL = "WHIRLPOOL"
}

object encryption_algo_ov extends OpenVocab {
  val AES128_ECB = "AES128-ECB"
  val AES128_CBC = "AES128-CBC"
  val AES128_CFB = "AES128-CFB"
  val AES128_OFB = "AES128-OFB"
  val AES128_CTR = "AES128-CTR"
  val AES128_XTS = "AES128-XTS"
  val AES128_GCM = "AES128-GCM"
  val Salsa20 = "Salsa20"
  val Salsa12 = "Salsa12"
  val Salsa8 = "Salsa8"
  val ChaCha20_Poly1305 = "ChaCha20-Poly1305"
  val ChaCha20 = "ChaCha20"
  val DES_CBC = "DES-CBC"
  val DES3_CBC = "3DES-CBC" // <---- 3DES_CBC
  val DES_ECB = "DES-ECB"
  val DES3_ECB = "3DES-ECB" // <---- 3DES_ECB
  val CAST128_CBC = "CAST128-CBC"
  val RSA = "RSA"
  val DSA = "DSA"
}
//---------------------------------------------------------------------------
//-------------------for Relationships---------------------------------------
//---------------------------------------------------------------------------

// todo not an OpenVocab
object relationship_type extends OpenVocab {
  val uses = "uses"
  val targets = "targets"
  val indicates = "indicates"
  val mitigates = "mitigates"
  val attributed_to = "attributed-to"
  val variant_of = "variant-of"
  val duplicate_of = "duplicate-of"
  val derived_from = "derived-from"
  val related_to = "related-to"
  val impersonates = "impersonates"
}