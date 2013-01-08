package javacode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import com.blipsystems.blipnet.api.blipserver.BlipServerAccessException;
import com.blipsystems.blipnet.api.blipserver.BlipServerConnectionException;

public class TBService {
	public static String iSpeechVOICE_LIST[] = { "usenglishfemale",
			"usenglishmale", "ukenglishfemale", "ukenglishmale",
			"auenglishfemale", "usspanishfemale", "usspanishmale",
			"chchinesefemale", "chchinesemale", "hkchinesefemale",
			"twchinesefemale", "jpjapanesefemale", "jpjapanesemale",
			"krkoreanfemale", "krkoreanmale", "caenglishfemale",
			"huhungarianfemale", "brportuguesefemale", "eurportuguesefemale",
			"eurportuguesemale", "eurspanishfemale", "eurspanishmale",
			"eurcatalanfemale", "eurczechfemale", "eurdanishfemale",
			"eurfinnishfemale", "eurfrenchfemale", "eurfrenchmale",
			"eurnorwegianfemale", "eurdutchfemale", "eurpolishfemale",
			"euritalianfemale", "euritalianmale", "eurturkishfemale",
			"eurturkishmale", "eurgreekfemale", "eurgermanfemale",
			"eurgermanmale", "rurussianfemale", "rurussianmale",
			"swswedishfemale", "cafrenchfemale", "cafrenchmale", "arabicmale" };
	static String acapelaVOICE_LIST[][] = {
			// { "ar_SA", "Arabic (Saudi Arabia)", "Leila", "HQ", "F",
			// "leila22k" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Leila", "HQ", "F", "leila8k"
			// },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Leila", "HQ", "F",
			// "leila8ka" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Leila", "HQ", "F",
			// "leila8kmu" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Mehdi", "HQ", "M",
			// "mehdi22k" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Mehdi", "HQ", "M", "mehdi8k"
			// },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Mehdi", "HQ", "M",
			// "mehdi8ka" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Mehdi", "HQ", "M",
			// "mehdi8kmu" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Nizar", "HQ", "M",
			// "nizar22k" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Nizar", "HQ", "M", "nizar8k"
			// },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Nizar", "HQ", "M",
			// "nizar8ka" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Nizar", "HQ", "M",
			// "nizar8kmu" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Salma", "HQ", "F",
			// "salma22k" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Salma", "HQ", "F", "salma8k"
			// },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Salma", "HQ", "F",
			// "salma8ka" },
			// { "ar_SA", "Arabic (Saudi Arabia)", "Salma", "HQ", "F",
			// "salma8kmu" },
			// { "ca_ES", "Catalan (Spain)", "Laia", "HQ", "F", "laia22k" },
			// { "ca_ES", "Catalan (Spain)", "Laia", "HQ", "F", "laia8k" },
			// { "ca_ES", "Catalan (Spain)", "Laia", "HQ", "F", "laia8ka" },
			// { "ca_ES", "Catalan (Spain)", "Laia", "HQ", "F", "laia8kmu" },
			// { "cs_CZ", "Czech", "Eliska", "HQ", "F", "eliska22k" },
			// { "cs_CZ", "Czech", "Eliska", "HQ", "F", "eliska8k" },
			// { "cs_CZ", "Czech", "Eliska", "HQ", "F", "eliska8ka" },
			// { "cs_CZ", "Czech", "Eliska", "HQ", "F", "eliska8kmu" },
			{ "da_DK", "Danish", "Mette", "HQ", "F", "mette22k" },
			// { "da_DK", "Danish", "Mette", "HQ", "F", "mette8k" },
			// { "da_DK", "Danish", "Mette", "HQ", "F", "mette8ka" },
			// { "da_DK", "Danish", "Mette", "HQ", "F", "mette8kmu" },
			{ "da_DK", "Danish", "Rasmus", "HQ", "M", "rasmus22k" },
			// { "da_DK", "Danish", "Rasmus", "HQ", "M", "rasmus8k" },
			// { "da_DK", "Danish", "Rasmus", "HQ", "M", "rasmus8ka" },
			// { "da_DK", "Danish", "Rasmus", "HQ", "M", "rasmus8kmu" },
			// { "nl_BE", "Dutch (Belgium)", "Jeroen", "HQ", "M", "jeroen22k" },
			// { "nl_BE", "Dutch (Belgium)", "Jeroen", "HQ", "M", "jeroen8k" },
			// { "nl_BE", "Dutch (Belgium)", "Jeroen", "HQ", "M", "jeroen8ka" },
			// { "nl_BE", "Dutch (Belgium)", "Jeroen", "HQ", "M", "jeroen8kmu"
			// },
			// { "nl_BE", "Dutch (Belgium)", "Sofie", "HQ", "F", "sofie22k" },
			// { "nl_BE", "Dutch (Belgium)", "Sofie", "HQ", "F", "sofie8k" },
			// { "nl_BE", "Dutch (Belgium)", "Sofie", "HQ", "F", "sofie8ka" },
			// { "nl_BE", "Dutch (Belgium)", "Sofie", "HQ", "F", "sofie8kmu" },
			// { "nl_BE", "Dutch (Belgium)", "Zoe", "HQ", "F", "zoe22k" },
			// { "nl_BE", "Dutch (Belgium)", "Zoe", "HQ", "F", "zoe8k" },
			// { "nl_BE", "Dutch (Belgium)", "Zoe", "HQ", "F", "zoe8ka" },
			// { "nl_BE", "Dutch (Belgium)", "Zoe", "HQ", "F", "zoe8kmu" },
			// { "nl_NL", "Dutch (Netherlands)", "Daan", "HQ", "M", "daan22k" },
			// { "nl_NL", "Dutch (Netherlands)", "Daan", "HQ", "M", "daan8k" },
			// { "nl_NL", "Dutch (Netherlands)", "Daan", "HQ", "M", "daan8ka" },
			// { "nl_NL", "Dutch (Netherlands)", "Daan", "HQ", "M", "daan8kmu"
			// },
			// { "nl_NL", "Dutch (Netherlands)", "Femke", "HQ", "F", "femke22k"
			// },
			// { "nl_NL", "Dutch (Netherlands)", "Femke", "HQ", "F", "femke8k"
			// },
			// { "nl_NL", "Dutch (Netherlands)", "Femke", "HQ", "F", "femke8ka"
			// },
			// { "nl_NL", "Dutch (Netherlands)", "Femke", "HQ", "F", "femke8kmu"
			// },
			// { "nl_NL", "Dutch (Netherlands)", "Jasmijn", "HQ", "F",
			// "jasmijn22k" },
			// { "nl_NL", "Dutch (Netherlands)", "Jasmijn", "HQ", "F",
			// "jasmijn8k" },
			// { "nl_NL", "Dutch (Netherlands)", "Jasmijn", "HQ", "F",
			// "jasmijn8ka" },
			// { "nl_NL", "Dutch (Netherlands)", "Jasmijn", "HQ", "F",
			// "jasmijn8kmu" },
			// { "nl_NL", "Dutch (Netherlands)", "Max", "HQ", "M", "max22k" },
			// { "nl_NL", "Dutch (Netherlands)", "Max", "HQ", "M", "max8k" },
			// { "nl_NL", "Dutch (Netherlands)", "Max", "HQ", "M", "max8ka" },
			// { "nl_NL", "Dutch (Netherlands)", "Max", "HQ", "M", "max8kmu" },
			// { "en_IN", "English (India)", "Deepa", "HQ", "F", "deepa22k" },
			// { "en_IN", "English (India)", "Deepa", "HQ", "F", "deepa8k" },
			// { "en_IN", "English (India)", "Deepa", "HQ", "F", "deepa8ka" },
			// { "en_IN", "English (India)", "Deepa", "HQ", "F", "deepa8kmu" },
			{ "en_GB", "English (UK)", "Graham", "HQ", "M", "graham22k" },
			// { "en_GB", "English (UK)", "Graham", "HQ", "M", "graham8k" },
			// { "en_GB", "English (UK)", "Graham", "HQ", "M", "graham8ka" },
			// { "en_GB", "English (UK)", "Graham", "HQ", "M", "graham8kmu" },
			// { "en_GB", "English (UK)", "Lucy", "HQ", "F", "lucy22k" },
			// { "en_GB", "English (UK)", "Lucy", "HQ", "F", "lucy8k" },
			// { "en_GB", "English (UK)", "Lucy", "HQ", "F", "lucy8ka" },
			// { "en_GB", "English (UK)", "Lucy", "HQ", "F", "lucy8kmu" },
			// { "en_GB", "English (UK)", "Nizareng", "HQ", "M", "nizareng22k"
			// },
			// { "en_GB", "English (UK)", "Nizareng", "HQ", "M", "nizareng8k" },
			// { "en_GB", "English (UK)", "Nizareng", "HQ", "M", "nizareng8ka"
			// },
			// { "en_GB", "English (UK)", "Nizareng", "HQ", "M", "nizareng8kmu"
			// },
			// { "en_GB", "English (UK)", "Peter", "HQ", "M", "peter22k" },
			// { "en_GB", "English (UK)", "Peter", "HQ", "M", "peter8k" },
			// { "en_GB", "English (UK)", "Peter", "HQ", "M", "peter8ka" },
			// { "en_GB", "English (UK)", "Peter", "HQ", "M", "peter8kmu" },
			// { "en_GB", "English (UK)", "QueenElizabeth", "HQ", "F",
			// "queenelizabeth22k" },
			// { "en_GB", "English (UK)", "QueenElizabeth", "HQ", "F",
			// "queenelizabeth8k" },
			// { "en_GB", "English (UK)", "QueenElizabeth", "HQ", "F",
			// "queenelizabeth8ka" },
			// { "en_GB", "English (UK)", "QueenElizabeth", "HQ", "F",
			// "queenelizabeth8kmu" },
			{ "en_GB", "English (UK)", "Rachel", "HQ", "F", "rachel22k" },
			// { "en_GB", "English (UK)", "Rachel", "HQ", "F", "rachel8k" },
			// { "en_GB", "English (UK)", "Rachel", "HQ", "F", "rachel8ka" },
			// { "en_GB", "English (UK)", "Rachel", "HQ", "F", "rachel8kmu" },
			// { "en_US", "English (USA)", "Heather", "HQ", "F", "heather22k" },
			// { "en_US", "English (USA)", "Heather", "HQ", "F", "heather8k" },
			// { "en_US", "English (USA)", "Heather", "HQ", "F", "heather8ka" },
			// { "en_US", "English (USA)", "Heather", "HQ", "F", "heather8kmu"
			// },
			// { "en_US", "English (USA)", "Kenny", "HQ", "M", "kenny22k" },
			// { "en_US", "English (USA)", "Kenny", "HQ", "M", "kenny8k" },
			// { "en_US", "English (USA)", "Kenny", "HQ", "M", "kenny8ka" },
			// { "en_US", "English (USA)", "Kenny", "HQ", "M", "kenny8kmu" },
			// { "en_US", "English (USA)", "Laura", "HQ", "F", "laura22k" },
			// { "en_US", "English (USA)", "Laura", "HQ", "F", "laura8k" },
			// { "en_US", "English (USA)", "Laura", "HQ", "F", "laura8ka" },
			// { "en_US", "English (USA)", "Laura", "HQ", "F", "laura8kmu" },
			// { "en_US", "English (USA)", "Micah", "HQ", "M", "micah22k" },
			// { "en_US", "English (USA)", "Micah", "HQ", "M", "micah8k" },
			// { "en_US", "English (USA)", "Micah", "HQ", "M", "micah8ka" },
			// { "en_US", "English (USA)", "Micah", "HQ", "M", "micah8kmu" },
			// { "en_US", "English (USA)", "Nelly", "HQ", "F", "nelly22k" },
			// { "en_US", "English (USA)", "Nelly", "HQ", "F", "nelly8k" },
			// { "en_US", "English (USA)", "Nelly", "HQ", "F", "nelly8ka" },
			// { "en_US", "English (USA)", "Nelly", "HQ", "F", "nelly8kmu" },
			// { "en_US", "English (USA)", "Ryan", "HQ", "M", "ryan22k" },
			// { "en_US", "English (USA)", "Ryan", "HQ", "M", "ryan8k" },
			// { "en_US", "English (USA)", "Ryan", "HQ", "M", "ryan8ka" },
			// { "en_US", "English (USA)", "Ryan", "HQ", "M", "ryan8kmu" },
			{ "en_US", "English (USA)", "Saul", "HQ", "M", "saul22k" },
			// { "en_US", "English (USA)", "Saul", "HQ", "M", "saul8ka" },
			// { "en_US", "English (USA)", "Saul", "HQ", "M", "saul8kmu" },
			{ "en_US", "English (USA)", "Tracy", "HQ", "F", "tracy22k" },
			// { "en_US", "English (USA)", "Tracy", "HQ", "F", "tracy8k" },
			// { "en_US", "English (USA)", "Tracy", "HQ", "F", "tracy8ka" },
			// { "en_US", "English (USA)", "Tracy", "HQ", "F", "tracy8kmu" },
			{ "fi_FI", "Finnish", "Sanna", "HQ", "F", "sanna22k" },
			// { "fi_FI", "Finnish", "Sanna", "HQ", "F", "sanna8k" },
			// { "fi_FI", "Finnish", "Sanna", "HQ", "F", "sanna8ka" },
			// { "fi_FI", "Finnish", "Sanna", "HQ", "F", "sanna8kmu" },
			// { "fr_BE", "French (Belgium)", "Justine", "HQ", "F", "justine22k"
			// },
			// { "fr_BE", "French (Belgium)", "Justine", "HQ", "F", "justine8k"
			// },
			// { "fr_BE", "French (Belgium)", "Justine", "HQ", "F", "justine8ka"
			// },
			// { "fr_BE", "French (Belgium)", "Justine", "HQ", "F",
			// "justine8kmu" },
			// { "fr_CA", "French (Canada)", "Louise", "HQ", "F", "louise22k" },
			// { "fr_CA", "French (Canada)", "Louise", "HQ", "F", "louise8k" },
			// { "fr_CA", "French (Canada)", "Louise", "HQ", "F", "louise8ka" },
			// { "fr_CA", "French (Canada)", "Louise", "HQ", "F", "louise8kmu"
			// },
			// { "fr_FR", "French (France)", "Alice", "HQ", "F", "alice22k" },
			// { "fr_FR", "French (France)", "Alice", "HQ", "F", "alice8k" },
			// { "fr_FR", "French (France)", "Alice", "HQ", "F", "alice8ka" },
			// { "fr_FR", "French (France)", "Alice", "HQ", "F", "alice8kmu" },
			// { "fr_FR", "French (France)", "Antoine", "HQ", "M", "antoine22k"
			// },
			// { "fr_FR", "French (France)", "Antoine", "HQ", "M", "antoine8k"
			// },
			// { "fr_FR", "French (France)", "Antoine", "HQ", "M", "antoine8ka"
			// },
			// { "fr_FR", "French (France)", "Antoine", "HQ", "M", "antoine8kmu"
			// },
			// { "fr_FR", "French (France)", "Bruno", "HQ", "M", "bruno22k" },
			// { "fr_FR", "French (France)", "Bruno", "HQ", "M", "bruno8k" },
			// { "fr_FR", "French (France)", "Bruno", "HQ", "M", "bruno8ka" },
			// { "fr_FR", "French (France)", "Bruno", "HQ", "M", "bruno8kmu" },
			// { "fr_FR", "French (France)", "Claire", "HQ", "F", "claire22k" },
			// { "fr_FR", "French (France)", "Claire", "HQ", "F", "claire8k" },
			// { "fr_FR", "French (France)", "Claire", "HQ", "F", "claire8ka" },
			// { "fr_FR", "French (France)", "Claire", "HQ", "F", "claire8kmu"
			// },
			// { "fr_FR", "French (France)", "Julie", "HQ", "F", "julie22k" },
			// { "fr_FR", "French (France)", "Julie", "HQ", "F", "julie8k" },
			// { "fr_FR", "French (France)", "Julie", "HQ", "F", "julie8ka" },
			// { "fr_FR", "French (France)", "Julie", "HQ", "F", "julie8kmu" },
			// { "fr_FR", "French (France)", "Margaux", "HQ", "F", "margaux22k"
			// },
			// { "fr_FR", "French (France)", "Margaux", "HQ", "F", "margaux8k"
			// },
			// { "fr_FR", "French (France)", "Margaux", "HQ", "F", "margaux8ka"
			// },
			// { "fr_FR", "French (France)", "Margaux", "HQ", "F", "margaux8kmu"
			// },
			// { "fr_FR", "French (France)", "Robot", "HQ", "M", "robot22k" },
			// { "de_DE", "German", "Andreas", "HQ", "M", "andreas22k" },
			// { "de_DE", "German", "Andreas", "HQ", "M", "andreas8k" },
			// { "de_DE", "German", "Andreas", "HQ", "M", "andreas8ka" },
			// { "de_DE", "German", "Andreas", "HQ", "M", "andreas8kmu" },
			// { "de_DE", "German", "Julia", "HQ", "F", "julia22k" },
			// { "de_DE", "German", "Julia", "HQ", "F", "julia8k" },
			// { "de_DE", "German", "Julia", "HQ", "F", "julia8ka" },
			// { "de_DE", "German", "Julia", "HQ", "F", "julia8kmu" },
			// { "de_DE", "German", "Klaus", "HQ", "M", "klaus22k" },
			// { "de_DE", "German", "Klaus", "HQ", "M", "klaus8k" },
			// { "de_DE", "German", "Klaus", "HQ", "M", "klaus8ka" },
			// { "de_DE", "German", "Klaus", "HQ", "M", "klaus8kmu" },
			// { "de_DE", "German", "Sarah", "HQ", "F", "sarah22k" },
			// { "de_DE", "German", "Sarah", "HQ", "F", "sarah8k" },
			// { "de_DE", "German", "Sarah", "HQ", "F", "sarah8ka" },
			// { "de_DE", "German", "Sarah", "HQ", "F", "sarah8kmu" },
			// { "el_GR", "Greek", "Dimitris", "HQ", "M", "dimitris22k" },
			// { "el_GR", "Greek", "Dimitris", "HQ", "M", "dimitris8k" },
			// { "el_GR", "Greek", "Dimitris", "HQ", "M", "dimitris8ka" },
			// { "el_GR", "Greek", "Dimitris", "HQ", "M", "dimitris8kmu" },
			// { "it_IT", "Italian", "Chiara", "HQ", "F", "chiara8k" },
			// { "it_IT", "Italian", "Chiara", "HQ", "F", "chiara8ka" },
			// { "it_IT", "Italian", "Chiara", "HQ", "F", "chiara8kmu" },
			// { "it_IT", "Italian", "Fabiana", "HQ", "F", "fabiana8k" },
			// { "it_IT", "Italian", "Fabiana", "HQ", "F", "fabiana8ka" },
			// { "it_IT", "Italian", "Fabiana", "HQ", "F", "fabiana8kmu" },
			// { "it_IT", "Italian", "Vittorio", "HQ", "M", "vittorio8k" },
			// { "it_IT", "Italian", "Vittorio", "HQ", "M", "vittorio8ka" },
			// { "it_IT", "Italian", "Vittorio", "HQ", "M", "vittorio8kmu" },
			// { "it_IT", "Italian", "chiara", "HQ", "F", "chiara22k" },
			// { "it_IT", "Italian", "fabiana", "HQ", "F", "fabiana22k" },
			// { "it_IT", "Italian", "vittorio", "HQ", "M", "vittorio22k" },
			// { "no_NO", "Norwegian", "Bente", "HQ", "F", "bente22k" },
			// { "no_NO", "Norwegian", "Bente", "HQ", "F", "bente8k" },
			// { "no_NO", "Norwegian", "Bente", "HQ", "F", "bente8ka" },
			// { "no_NO", "Norwegian", "Bente", "HQ", "F", "bente8kmu" },
			{ "no_NO", "Norwegian", "Kari", "HQ", "F", "kari22k" },
			// { "no_NO", "Norwegian", "Kari", "HQ", "F", "kari8k" },
			// { "no_NO", "Norwegian", "Kari", "HQ", "F", "kari8ka" },
			// { "no_NO", "Norwegian", "Kari", "HQ", "F", "kari8kmu" },
			{ "no_NO", "Norwegian", "Olav", "HQ", "M", "olav22k" },
			// { "no_NO", "Norwegian", "Olav", "HQ", "M", "olav8k" },
			// { "no_NO", "Norwegian", "Olav", "HQ", "M", "olav8ka" },
			// { "no_NO", "Norwegian", "Olav", "HQ", "M", "olav8kmu" },
			// { "pl_PL", "Polish", "Ania", "HQ", "F", "ania22k" },
			// { "pl_PL", "Polish", "Ania", "HQ", "F", "ania8k" },
			// { "pl_PL", "Polish", "Ania", "HQ", "F", "ania8ka" },
			// { "pl_PL", "Polish", "Ania", "HQ", "F", "ania8kmu" },
			// { "pt_BR", "Portuguese (Brazil)", "Marcia", "HQ", "F",
			// "marcia22k" },
			// { "pt_BR", "Portuguese (Brazil)", "Marcia", "HQ", "F", "marcia8k"
			// },
			// { "pt_BR", "Portuguese (Brazil)", "Marcia", "HQ", "F",
			// "marcia8ka" },
			// { "pt_BR", "Portuguese (Brazil)", "Marcia", "HQ", "F",
			// "marcia8kmu" },
			// { "pt_PT", "Portuguese (Portugal)", "Celia", "HQ", "F",
			// "celia22k" },
			// { "pt_PT", "Portuguese (Portugal)", "Celia", "HQ", "F", "celia8k"
			// },
			// { "pt_PT", "Portuguese (Portugal)", "Celia", "HQ", "F",
			// "celia8ka" },
			// { "pt_PT", "Portuguese (Portugal)", "Celia", "HQ", "F",
			// "celia8kmu" },
			// { "ru_RU", "Russian", "Alyona", "HQ", "F", "alyona22k" },
			// { "ru_RU", "Russian", "Alyona", "HQ", "F", "alyona8k" },
			// { "ru_RU", "Russian", "Alyona", "HQ", "F", "alyona8ka" },
			// { "ru_RU", "Russian", "Alyona", "HQ", "F", "alyona8kmu" },
			// { "sc_SE", "Scanian (Sweden)", "Mia", "HQ", "F", "mia22k" },
			// { "sc_SE", "Scanian (Sweden)", "Mia", "HQ", "F", "mia8k" },
			// { "sc_SE", "Scanian (Sweden)", "Mia", "HQ", "F", "mia8ka" },
			// { "sc_SE", "Scanian (Sweden)", "Mia", "HQ", "F", "mia8kmu" },
			// { "es_ES", "Spanish (Spain)", "Antonio", "HQ", "M", "antonio22k"
			// },
			// { "es_ES", "Spanish (Spain)", "Antonio", "HQ", "M", "antonio8k"
			// },
			// { "es_ES", "Spanish (Spain)", "Antonio", "HQ", "M", "antonio8ka"
			// },
			// { "es_ES", "Spanish (Spain)", "Antonio", "HQ", "M", "antonio8kmu"
			// },
			// { "es_ES", "Spanish (Spain)", "Ines", "HQ", "F", "ines22k" },
			// { "es_ES", "Spanish (Spain)", "Ines", "HQ", "F", "ines8k" },
			// { "es_ES", "Spanish (Spain)", "Ines", "HQ", "F", "ines8ka" },
			// { "es_ES", "Spanish (Spain)", "Ines", "HQ", "F", "ines8kmu" },
			// { "es_ES", "Spanish (Spain)", "Maria", "HQ", "F", "maria22k" },
			// { "es_ES", "Spanish (Spain)", "Maria", "HQ", "F", "maria8k" },
			// { "es_ES", "Spanish (Spain)", "Maria", "HQ", "F", "maria8ka" },
			// { "es_ES", "Spanish (Spain)", "Maria", "HQ", "F", "maria8kmu" },
			// { "es_US", "Spanish (USA)", "Rosa", "HQ", "F", "rosa22k" },
			// { "es_US", "Spanish (USA)", "Rosa", "HQ", "F", "rosa8k" },
			// { "es_US", "Spanish (USA)", "Rosa", "HQ", "F", "rosa8ka" },
			// { "es_US", "Spanish (USA)", "Rosa", "HQ", "F", "rosa8kmu" },
			{ "sv_SE", "Swedish", "Elin", "HQ", "F", "elin22k" },
			// { "sv_SE", "Swedish", "Elin", "HQ", "F", "elin8k" },
			// { "sv_SE", "Swedish", "Elin", "HQ", "F", "elin8ka" },
			// { "sv_SE", "Swedish", "Elin", "HQ", "F", "elin8kmu" },
			{ "sv_SE", "Swedish", "Emil", "HQ", "M", "emil22k" },
			// { "sv_SE", "Swedish", "Emil", "HQ", "M", "emil8k" },
			// { "sv_SE", "Swedish", "Emil", "HQ", "M", "emil8ka" },
			// { "sv_SE", "Swedish", "Emil", "HQ", "M", "emil8kmu" },
			// { "sv_SE", "Swedish", "Emma", "HQ", "F", "emma22k" },
			// { "sv_SE", "Swedish", "Emma", "HQ", "F", "emma8k" },
			// { "sv_SE", "Swedish", "Emma", "HQ", "F", "emma8ka" },
			// { "sv_SE", "Swedish", "Emma", "HQ", "F", "emma8kmu" },
			// { "sv_SE", "Swedish", "Erik", "HQ", "M", "erik22k" },
			// { "sv_SE", "Swedish", "Erik", "HQ", "M", "erik8k" },
			// { "sv_SE", "Swedish", "Erik", "HQ", "M", "erik8ka" },
			// { "sv_SE", "Swedish", "Erik", "HQ", "M", "erik8kmu" },
			// { "sv_FI", "Swedish (Finland)", "Samuel", "HQ", "M", "samuel22k"
			// },
			// { "sv_FI", "Swedish (Finland)", "Samuel", "HQ", "M", "samuel8k"
			// },
			// { "sv_FI", "Swedish (Finland)", "Samuel", "HQ", "M", "samuel8ka"
			// },
			// { "sv_FI", "Swedish (Finland)", "Samuel", "HQ", "M", "samuel8kmu"
			// },
			// { "gb_SE", "Swedish - Gothenburg (Sweden)", "Kal", "HQ", "M",
			// "kal22k" },
			// { "gb_SE", "Swedish - Gothenburg (Sweden)", "Kal", "HQ", "M",
			// "kal8k" },
			// { "gb_SE", "Swedish - Gothenburg (Sweden)", "Kal", "HQ", "M",
			// "kal8ka" },
			// { "gb_SE", "Swedish - Gothenburg (Sweden)", "Kal", "HQ", "M",
			// "kal8kmu" },
			// { "tr_TR", "Turkish", "Ipek", "HQ", "F", "ipek22k" },
			// { "tr_TR", "Turkish", "Ipek", "HQ", "F", "ipek8k" },
			// { "tr_TR", "Turkish", "Ipek", "HQ", "F", "ipek8ka" },
			{ "tr_TR", "Turkish", "Ipek", "HQ", "F", "ipek8kmu" } };
	private static DatabaseOperation db = null;
	private static long disappearInterval = 120000;
	private static boolean sendMessageWithoutApproval = true;
	static boolean resendMessageAutomatically = false;

	// Get scan result
	public static LinkedList<UserDevice> getDevicesList() {
		try {
			SearchDevice.startSearchingService();
		} catch (BlipServerAccessException e) {
			e.printStackTrace();
		} catch (BlipServerConnectionException e) {
			e.printStackTrace();
		}
		return SearchDevice.devicesList;
	}

	// OPP
	public static String sendFile(String terminalMac, String content,
			String language, String gender) {
		try {
			return (new OPPService()).sendMessageFile(terminalMac, content,
					language, gender);
		} catch (Exception e) {
			return e.toString();
		}
	}

	// SPP
	public static String sendCommand(String terminalMac, String content) {
		return (new SPPService()).sendCommand(terminalMac, content);
	}

	// DB update
	public synchronized static String dbUpdate(String sqlContent) {
		Connection connect = null;
		Statement statement = null;
		if (db == null)
			db = new DatabaseOperation();
		connect = db.getConnection();
		try {
			statement = connect.createStatement();
			statement.executeUpdate(sqlContent);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.toString();
		}
		try {
			if (statement != null) {
				statement.close();
			}
			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			return e.toString();
		}

		return "True";
	}

	// DB query
	public static AConnection dbQuery(String sqlquery) {
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		if (db == null)
			db = new DatabaseOperation();
		connect = db.getConnection();
		try {
			statement = connect.createStatement();
			resultSet = statement.executeQuery(sqlquery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new AConnection(resultSet, statement, connect);
	}

	public static boolean validateUser(String username, String passwd) {
		// System.out.println(username+passwd);
		AConnection aConnection = dbQuery("select * from talkingbadge.user where name='"
				+ username + "' AND passwd='" + passwd + "';");
		ResultSet resultSet = aConnection.resultSet;
		try {
			if (resultSet.next()) {
				aConnection.closeConnection();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		aConnection.closeConnection();
		return false;
	}

	public static String findDeviceLocation(String mac) {
		LinkedList<UserDevice> detectedDevices = getDevicesList();
		// System.out.println(detectedDevices);
		Date curDate = new Date();
		for (UserDevice ud : detectedDevices) {
			// System.out.println(mac+ud.getBlipMac().toString()+detectedDevices);
			if (ud.getCd().getTerminalID().toString().equals(mac)) {
				long interval = curDate.getTime()
						- ud.getDetectTime().getTime();
				// System.out.println(interval);
				if (interval < disappearInterval)
					return ud.getBlipCurZoneName();
			}
		}
		return "null";
	}

	public static LinkedList<String> findUsersLocation(boolean OnlyHaveDevice) {
		AConnection aConnection = dbQuery("select * from talkingbadge.user;");
		ResultSet resultSet = aConnection.resultSet;
		LinkedList<String> result = new LinkedList<String>();

		try {
			while (resultSet.next()) {
				if (OnlyHaveDevice) {
					if (resultSet.getString("mac").equals("NULL"))
						continue;
				}
				String name = resultSet.getString("name");
				String mac = resultSet.getString("mac");
				String position = findDeviceLocation(mac);

				result.add(name + " " + mac + " " + position);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (resultSet != null) {
			aConnection.closeConnection();
		}
		return result;
	}

	public synchronized static String sendMessageForTB(String fromUser,
			String mac, String msg, String language, String gender,
			boolean fromAdmin) {
		String s1 = "Waiting for approval ";
		String s2 = "";
		if (sendMessageWithoutApproval || fromAdmin) {
			s1 = sendFile(mac, msg, language, gender);

			if (s1.contains("success. The file is C:\\wusheng\\res\\")) {
				String name = s1.substring(s1.indexOf("\\res\\") + 5);
				// System.out.println(name);
				for (double i = 0; i < 999999999; i = i + 0.5) {
				}
				s2 = sendCommand(mac, "TB PLAYSOUND " + name) + "<br>";
			}
			// for (double i = 0; i < 999999999; i = i + 1) {}
		}
		String sql = "INSERT INTO messagerecord values (default, '"
				+ new Date()
				+ "', '"
				+ fromUser
				+ "', '"
				+ mac
				+ "','"
				+ msg
				+ "', '"
				+ language
				+ "', '"
				+ gender
				+ "', '"
				+ (s1.equals("Waiting for approval ") ? -2 : (((s1 + s2)
						.toLowerCase().contains("failed")) ? 0 : 1)) + "', '"
				+ s1 + ";" + s2 + "');";
		// System.out.println(sql);
		dbUpdate(sql);

		if ((!s1.equals("Waiting for approval "))
				&& (s2.contains("FAILED") | s2.contains("failed"))
				&& resendMessageAutomatically) {
			s2 += "This message will be send again automatically when the device is detected again.<br>";
		}
		return s1.replace("C:\\wusheng\\res\\", "") + "<br>" + s2;

	}

	public static void reSendMessage(UserDevice userDevice, boolean fromadmin) {
		AConnection aConnection = TBService
				.dbQuery("select * from talkingbadge.messagerecord where tomac='"
						+ userDevice.getCd().getTerminalID()
						+ "' AND success=0;");
		ResultSet aMessage = aConnection.resultSet;
		try {
			if (aMessage.next()) {
				// System.out.println(aMessage.getInt("id"));
				TBService
						.dbUpdate("UPDATE messagerecord SET success=-1 WHERE id='"
								+ aMessage.getInt("id") + "';");
				TBService.sendMessageForTBs(aMessage.getString("fromname"),
						new String[]{aMessage.getString("tomac")},
						aMessage.getString("content"),
						aMessage.getString("language"),
						aMessage.getString("gender"), fromadmin);
				System.out.println("Resent to"+ aMessage.getString("tomac"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		aConnection.closeConnection();
	}

	public synchronized static String sendPreRecordMessageForTB(
			String fromUser, String mac, String msg, String language,
			String gender) {
		String s1 = sendCommand(mac, "TB PLAYSOUND " + msg) + "<br>";
		String sql = "INSERT INTO messagerecord values (default, '"
				+ new Date() + "', '" + fromUser + "', '" + mac + "','" + msg
				+ "', '" + language + "', '" + gender + "', '" + 1 + "', '"
				+ s1 + "');";
		// System.out.println(sql);
		dbUpdate(sql);
		return s1;
	}

	public synchronized static void sendPreRecordMessageForTBAutomatically(
			String mac, String zone) {
		// System.out.println(mac+zone);
		AConnection aConnection = dbQuery("select * from talkingbadge.user where mac='"
				+ mac + "';");
		ResultSet resultSet = aConnection.resultSet;
		try {
			if (resultSet.next()) {
				if (resultSet.getString("name").startsWith("badge")) {
					if (zone.equals("itu.zone2.zone2b")
							|| zone.equals("itu.zone2.zone2c")
							|| zone.equals("itu.zone2.zone2d")
							|| zone.equals("itu.zone2.zone2e"))
						sendPreRecordMessageForTB("system", mac,
								"itu.zone2.mp3", "", "");
					//else if (zone.equals("itu.zone0.zoneaud2"))
						//sendPreRecordMessageForTB("system", mac,
							//	"itu.zone0.zoneaud2.mp3", "", "");
					else if (zone.startsWith("itu.zone0.zoneanalog"))
						sendPreRecordMessageForTB("system", mac,
								"itu.zone0.zoneanalog.mp3", "", "");
					//else if (zone.startsWith("itu.zone0.zoneaud1"))
						//sendPreRecordMessageForTB("system", mac,
							//	"itu.zone0.zoneaud1.mp3", "", "");
					else if (zone.startsWith("itu.zone0.zonescroll"))
						sendPreRecordMessageForTB("system", mac,
								"itu.zone0.zonescroll.mp3", "", "");
					else if (zone.startsWith("itu.zone4.zone4c1"))
						sendPreRecordMessageForTB("system", mac,
								"itu.zone4.zone4c1.mp3", "", "");
					
					else if (zone.startsWith("itu.zone0.zonedornord"))
						sendPreRecordMessageForTB("system", mac,
								"itu.zone0.zonedornord.mp3", "", "");
					else if (zone.startsWith("itu.zone0.zonedorsyd"))
						sendPreRecordMessageForTB("system", mac,
								"itu.zone0.zonedorsyd.mp3", "", "");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		aConnection.closeConnection();

	}

	public static synchronized String sendMessageForTBs(String fromUser,
			String[] macs, String msg, String language, String gender,
			boolean fromAdmin) {
		if (msg.replaceAll(" ", "").isEmpty()) {
			return "Error: Empty message.";
		}
		String filePath = OPPService.iSpeechTTS(msg, language, gender);
		String replyMessage = "";
		for (String mac : macs) {
			String s1 = "Waiting for approval ";
			String s2 = "";
			if (sendMessageWithoutApproval || fromAdmin) {
				s1 = sendFile(mac, filePath);

				if (s1.contains("success. The file is C:\\wusheng\\res\\")) {
					String name = s1.substring(s1.indexOf("\\res\\") + 5);
					// System.out.println(name);
					for (double i = 0; i < 999999999; i = i + 0.5) {
					}
					s2 = sendCommand(mac, "TB PLAYSOUND " + name) + "<br>";
				}
				// for (double i = 0; i < 999999999; i = i + 1) {}
			}

			if ((!s1.equals("Waiting for approval "))
					&& (s2.contains("FAILED") | s2.contains("failed"))
					&& resendMessageAutomatically) {
				s2 += "This message will be send again automatically when the device is detected again.<br>";
			}
			String resultMessage = s1.replace("C:\\wusheng\\res\\", "")
					+ "<br>" + s2 + "<br>";
			if (s1 == "Waiting for approval ") {
				replyMessage += s1 + " failed<br>";
			} else if (resultMessage.toLowerCase().contains("failed")) {
				replyMessage += "Send message to " + mac + " failed<br>";
				if (resendMessageAutomatically)
					replyMessage += "This message will be send again automatically when the device is detected again.<br>";
			} else {
				replyMessage += "Send message to " + mac + " successfully<br>";
			}
			String sql = "INSERT INTO messagerecord values (default, '"
					+ new Date()
					+ "', '"
					+ fromUser
					+ "', '"
					+ mac
					+ "','"
					+ msg
					+ "', '"
					+ language
					+ "', '"
					+ gender
					+ "', '"
					+ (s1.equals("Waiting for approval ") ? -2
							: ((resultMessage.toLowerCase().contains("failed")) ? 0
									: 1)) + "', '" + s1 + ";" + s2 + "');";
			// System.out.println(sql);
			dbUpdate(sql);
		}
		return replyMessage;

	}

	private static String sendFile(String terminalMac, String filePath) {
		try {
			return (new OPPService()).sendMessageFile(terminalMac, filePath);
		} catch (Exception e) {
			return e.toString();
		}
	}
}
