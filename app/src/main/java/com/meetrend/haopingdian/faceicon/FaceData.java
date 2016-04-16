package com.meetrend.haopingdian.faceicon;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Faceicon;


/**
 * fine, I don't want to parse plist file, hardcoding
 * @author tigereye
 *
 */
/*
 *
#!/usr/bin/env
use warnings;
use v5.10;
open(my $in, "<", "_expression.plist") or die "cann't open $!";
my @lines = <$in>;
my $res;
my $text;
my $index = 0;

for (@lines) {
	if (/<key>(\d\d\d)<\/key>/) {	
		$res = $1;
		++$index 
	} 
	if (/<string>(\D*)<\/string>/) {
		$text = $1;
		++$index;
	}
	if ($index == 2) {
		say "Faceicon.fromResource(R.drawable.face$res, \"$text\"),"; 	
		$index = 0;
	}	
}

 */
public class FaceData {
	public static final Faceicon[] EMOJI = new Faceicon[] {
		Faceicon.fromCodePoint(0x1F600),
		Faceicon.fromCodePoint(0x1F602),
		Faceicon.fromCodePoint(0x1F603),
		Faceicon.fromCodePoint(0x1F699),
		Faceicon.fromCodePoint(0x1F60B),
		Faceicon.fromCodePoint(0x1F60C),
		Faceicon.fromCodePoint(0x1F610),
		Faceicon.fromCodePoint(0x1F6A5),
		Faceicon.fromCodePoint(0x1F6AB),
		Faceicon.fromCodePoint(0x1F640),
		Faceicon.fromCodePoint(0x1F639),
		Faceicon.fromCodePoint(0x1F615),
		Faceicon.fromCodePoint(0x1F616),
		Faceicon.fromCodePoint(0x1F617),
		Faceicon.fromCodePoint(0x1F609),
		Faceicon.fromCodePoint(0x1F61E),
		Faceicon.fromCodePoint(0x1F61F),
		Faceicon.fromCodePoint(0x1F623),
		Faceicon.fromCodePoint(0x1F621),
		Faceicon.fromCodePoint(0x1F692),
		null
	};
	
	public static final Faceicon[] QQ1= new Faceicon[] {
		Faceicon.fromResource(R.drawable.face001, "[微笑]"),
		Faceicon.fromResource(R.drawable.face002, "[撇嘴]"),
		Faceicon.fromResource(R.drawable.face003, "[色]"),
		Faceicon.fromResource(R.drawable.face004, "[发呆]"),
		Faceicon.fromResource(R.drawable.face005, "[得意]"),
		Faceicon.fromResource(R.drawable.face006, "[流泪]"),
		Faceicon.fromResource(R.drawable.face007, "[害羞]"),
		Faceicon.fromResource(R.drawable.face008, "[闭嘴]"),
		Faceicon.fromResource(R.drawable.face009, "[睡]"),
		Faceicon.fromResource(R.drawable.face010, "[大哭]"),
		Faceicon.fromResource(R.drawable.face011, "[尴尬]"),
		Faceicon.fromResource(R.drawable.face012, "[发怒]"),
		Faceicon.fromResource(R.drawable.face013, "[调皮]"),
		Faceicon.fromResource(R.drawable.face014, "[龇牙]"),
		Faceicon.fromResource(R.drawable.face015, "[惊讶]"),
		Faceicon.fromResource(R.drawable.face016, "[难过]"),
		Faceicon.fromResource(R.drawable.face017, "[酷]"),
		Faceicon.fromResource(R.drawable.face018, "[冷汗]"),
		Faceicon.fromResource(R.drawable.face019, "[抓狂]"),
		Faceicon.fromResource(R.drawable.face020, "[吐]"),
		null
	};
	
	public static final Faceicon[] QQ1_1 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face001, "/::)"),
		Faceicon.fromResource(R.drawable.face002, "/::~"),
		Faceicon.fromResource(R.drawable.face003, "/::B"),
		Faceicon.fromResource(R.drawable.face004, "/::|"),
		Faceicon.fromResource(R.drawable.face006, "/::|"),
		Faceicon.fromResource(R.drawable.face007, "/::&lt;"),
		Faceicon.fromResource(R.drawable.face008, "/::$"),
		Faceicon.fromResource(R.drawable.face009, "/::X"),
		Faceicon.fromResource(R.drawable.face010, "/::Z"),
		Faceicon.fromResource(R.drawable.face011, "/::&apos;("),
		Faceicon.fromResource(R.drawable.face012, "/::-|"),
		Faceicon.fromResource(R.drawable.face013, "/::@"),
		Faceicon.fromResource(R.drawable.face014, "/::P"),
		Faceicon.fromResource(R.drawable.face015, "/::D"),
		Faceicon.fromResource(R.drawable.face016, "/::O"),
		Faceicon.fromResource(R.drawable.face017, "/::("),
		Faceicon.fromResource(R.drawable.face018, "/:kuk"),
		Faceicon.fromResource(R.drawable.face019, "/:--b"),
		Faceicon.fromResource(R.drawable.face020, "/::Q"),
		Faceicon.fromResource(R.drawable.face021, "/::T"),
		null
	};
		
	public static final Faceicon[] QQ2 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face021, "[偷笑]"),
		Faceicon.fromResource(R.drawable.face022, "[愉快]"),
		Faceicon.fromResource(R.drawable.face023, "[白眼]"),
		Faceicon.fromResource(R.drawable.face024, "[傲慢]"),
		Faceicon.fromResource(R.drawable.face025, "[饥饿]"),
		Faceicon.fromResource(R.drawable.face026, "[困]"),
		Faceicon.fromResource(R.drawable.face027, "[惊恐]"),
		Faceicon.fromResource(R.drawable.face028, "[流汗]"),
		Faceicon.fromResource(R.drawable.face029, "[憨笑]"),
		Faceicon.fromResource(R.drawable.face030, "[悠闲]"),
		Faceicon.fromResource(R.drawable.face031, "[奋斗]"),
		Faceicon.fromResource(R.drawable.face032, "[咒骂]"),
		Faceicon.fromResource(R.drawable.face033, "[疑问]"),
		Faceicon.fromResource(R.drawable.face034, "[嘘]"),
		Faceicon.fromResource(R.drawable.face035, "[晕]"),
		Faceicon.fromResource(R.drawable.face036, "[疯了]"),
		Faceicon.fromResource(R.drawable.face037, "[衰]"),
		Faceicon.fromResource(R.drawable.face038, "[骷髅]"),
		Faceicon.fromResource(R.drawable.face039, "[敲打]"),
		Faceicon.fromResource(R.drawable.face040, "[再见]"),
		null
	};
	
	public static final Faceicon[] QQ2_1 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face022, "/:,@P"),
		Faceicon.fromResource(R.drawable.face023, "/:,@-D"),
		Faceicon.fromResource(R.drawable.face024, "/::d"),
		Faceicon.fromResource(R.drawable.face025, "/:,@o"),
		Faceicon.fromResource(R.drawable.face026, "/::g"),
		Faceicon.fromResource(R.drawable.face027, "/:|-)"),
		Faceicon.fromResource(R.drawable.face028, "/::!"),
		Faceicon.fromResource(R.drawable.face029, "/::L"),
		Faceicon.fromResource(R.drawable.face030, "/::&gt;"),
		Faceicon.fromResource(R.drawable.face031, "/::,@"),
		Faceicon.fromResource(R.drawable.face032, "/:,@f"),
		Faceicon.fromResource(R.drawable.face033, "/::-S"),
		Faceicon.fromResource(R.drawable.face034, "/:?"),
		Faceicon.fromResource(R.drawable.face035, "/:,@x"),
		Faceicon.fromResource(R.drawable.face036, "/:,@@"),
		Faceicon.fromResource(R.drawable.face037, "/:,@!"),
		Faceicon.fromResource(R.drawable.face038, "/:!!!"),
		Faceicon.fromResource(R.drawable.face039, "/:xx"),
		Faceicon.fromResource(R.drawable.face040, "/:bye"),
		null
	};
	
	public static final Faceicon[] QQ3 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face041, "[擦汗]"),
		Faceicon.fromResource(R.drawable.face042, "[抠鼻]"),
		Faceicon.fromResource(R.drawable.face043, "[鼓掌]"),
		Faceicon.fromResource(R.drawable.face044, "[糗大了]"),
		Faceicon.fromResource(R.drawable.face045, "[坏笑]"),
		Faceicon.fromResource(R.drawable.face046, "[左哼哼]"),
		Faceicon.fromResource(R.drawable.face047, "[右哼哼]"),
		Faceicon.fromResource(R.drawable.face048, "[哈欠]"),
		Faceicon.fromResource(R.drawable.face049, "[鄙视]"),
		Faceicon.fromResource(R.drawable.face050, "[委屈]"),
		Faceicon.fromResource(R.drawable.face051, "[快哭了]"),
		Faceicon.fromResource(R.drawable.face052, "[阴险]"),
		Faceicon.fromResource(R.drawable.face053, "[亲亲]"),
		Faceicon.fromResource(R.drawable.face054, "[吓]"),
		Faceicon.fromResource(R.drawable.face055, "[可怜]"),
		Faceicon.fromResource(R.drawable.face056, "[菜刀]"),
		Faceicon.fromResource(R.drawable.face057, "[西瓜]"),
		Faceicon.fromResource(R.drawable.face058, "[啤酒]"),
		Faceicon.fromResource(R.drawable.face059, "[篮球]"),
		Faceicon.fromResource(R.drawable.face060, "[乒乓]"),
		null
	};
	
	public static final Faceicon[] QQ3_1 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face041, "/:wipe"),
		Faceicon.fromResource(R.drawable.face042, "/:dig"),
		Faceicon.fromResource(R.drawable.face043, "/:handclap"),
		Faceicon.fromResource(R.drawable.face044, "/:&amp;-("),
		Faceicon.fromResource(R.drawable.face045, "/:B-)"),
		Faceicon.fromResource(R.drawable.face046, "/:&lt;@"),
		Faceicon.fromResource(R.drawable.face047, "/:@&gt;"),
		Faceicon.fromResource(R.drawable.face048, "/::-O"),
		Faceicon.fromResource(R.drawable.face049, "/:&gt;-|"),
		Faceicon.fromResource(R.drawable.face050, "/:P-("),
		Faceicon.fromResource(R.drawable.face051, "/::&apos;|"),
		Faceicon.fromResource(R.drawable.face052, "/:X-)"),
		Faceicon.fromResource(R.drawable.face053, "/::*"),
		Faceicon.fromResource(R.drawable.face054, "/:@x"),
		Faceicon.fromResource(R.drawable.face056, "/:@x"),
		Faceicon.fromResource(R.drawable.face057, "/:pd"),
		Faceicon.fromResource(R.drawable.face058, "/:&lt;W&gt;"),
		Faceicon.fromResource(R.drawable.face059, "/:beer"),
		Faceicon.fromResource(R.drawable.face060, "/:basketb"),
		null
	};
	
	public static final Faceicon[] QQ4 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face061, "[咖啡]"),
		Faceicon.fromResource(R.drawable.face062, "[饭]"),
		Faceicon.fromResource(R.drawable.face063, "[猪头]"),
		Faceicon.fromResource(R.drawable.face064, "[玫瑰]"),
		Faceicon.fromResource(R.drawable.face065, "[凋谢]"),
		Faceicon.fromResource(R.drawable.face066, "[嘴唇]"),
		Faceicon.fromResource(R.drawable.face067, "[爱心]"),
		Faceicon.fromResource(R.drawable.face068, "[心碎]"),
		Faceicon.fromResource(R.drawable.face069, "[蛋糕]"),
		Faceicon.fromResource(R.drawable.face070, "[闪电]"),
		Faceicon.fromResource(R.drawable.face071, "[炸弹]"),
		Faceicon.fromResource(R.drawable.face072, "[刀]"),
		Faceicon.fromResource(R.drawable.face073, "[足球]"),
		Faceicon.fromResource(R.drawable.face074, "[瓢虫]"),
		Faceicon.fromResource(R.drawable.face075, "[便便]"),
		Faceicon.fromResource(R.drawable.face076, "[月亮]"),
		Faceicon.fromResource(R.drawable.face077, "[太阳]"),
		Faceicon.fromResource(R.drawable.face078, "[礼物]"),
		Faceicon.fromResource(R.drawable.face079, "[拥抱]"),
		Faceicon.fromResource(R.drawable.face080, "[强]"),
		null
	};
	
	public static final Faceicon[] QQ4_1 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face061, "/:oo"),
		Faceicon.fromResource(R.drawable.face062, "/:coffee"),
		Faceicon.fromResource(R.drawable.face063, "/:eat"),
		Faceicon.fromResource(R.drawable.face064, "/:pig"),
		Faceicon.fromResource(R.drawable.face065, "/:rose"),
		Faceicon.fromResource(R.drawable.face066, "/:fade"),
		Faceicon.fromResource(R.drawable.face067, "/:showlove"),
		Faceicon.fromResource(R.drawable.face068, "/:heart"),
		Faceicon.fromResource(R.drawable.face069, "/:break"),
		Faceicon.fromResource(R.drawable.face070, "/:cake"),
		Faceicon.fromResource(R.drawable.face071, "/:lI"),
		Faceicon.fromResource(R.drawable.face072, "/:bome"),
		Faceicon.fromResource(R.drawable.face073, "/:kn"),
		Faceicon.fromResource(R.drawable.face074, "/:footb"),
		Faceicon.fromResource(R.drawable.face075, "/:ladybug"),
		Faceicon.fromResource(R.drawable.face076, "/:shit"),
		Faceicon.fromResource(R.drawable.face077, "/:moon"),
		Faceicon.fromResource(R.drawable.face078, "/:sun"),
		Faceicon.fromResource(R.drawable.face079, "/:gift"),
		Faceicon.fromResource(R.drawable.face080, "/:hug"),
		null
	};
	
	public static final Faceicon[] QQ5 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face081, "[弱]"),
		Faceicon.fromResource(R.drawable.face082, "[握手]"),
		Faceicon.fromResource(R.drawable.face083, "[胜利]"),
		Faceicon.fromResource(R.drawable.face084, "[抱拳]"),
		Faceicon.fromResource(R.drawable.face085, "[勾引]"),
		Faceicon.fromResource(R.drawable.face086, "[拳头]"),
		Faceicon.fromResource(R.drawable.face087, "[差劲]"),
		Faceicon.fromResource(R.drawable.face088, "[爱你]"),
		Faceicon.fromResource(R.drawable.face089, "[NO]"),
		Faceicon.fromResource(R.drawable.face090, "[OK]"),
		Faceicon.fromResource(R.drawable.face091, "[爱情]"),
		Faceicon.fromResource(R.drawable.face092, "[飞吻]"),
		Faceicon.fromResource(R.drawable.face093, "[跳跳]"),
		Faceicon.fromResource(R.drawable.face094, "[发抖]"),
		Faceicon.fromResource(R.drawable.face095, "[怄火]"),
		Faceicon.fromResource(R.drawable.face096, "[转圈]"),
		Faceicon.fromResource(R.drawable.face097, "[磕头]"),
		Faceicon.fromResource(R.drawable.face098, "[回头]"),
		Faceicon.fromResource(R.drawable.face099, "[跳绳]"),
		Faceicon.fromResource(R.drawable.face100, "[投降]"),
		null
	};
	
	public static final Faceicon[] QQ5_1 = new Faceicon[] {
		Faceicon.fromResource(R.drawable.face081, "/:strong"),
		Faceicon.fromResource(R.drawable.face082, "/:weak"),
		Faceicon.fromResource(R.drawable.face083, "/:share"),
		Faceicon.fromResource(R.drawable.face084, "/:v"),
		Faceicon.fromResource(R.drawable.face085, "/:@)"),
		Faceicon.fromResource(R.drawable.face086, "/:jj"),
		Faceicon.fromResource(R.drawable.face087, "/:@@"),
		Faceicon.fromResource(R.drawable.face088, "/:bad"),
		Faceicon.fromResource(R.drawable.face089, "/:lvu"),
		Faceicon.fromResource(R.drawable.face090, "/:No"),
		Faceicon.fromResource(R.drawable.face091, "/:ok"),
		Faceicon.fromResource(R.drawable.face092, "/:love"),
		Faceicon.fromResource(R.drawable.face093, "/:&lt;L&gt;"),
		Faceicon.fromResource(R.drawable.face094, "/:jump"),
		Faceicon.fromResource(R.drawable.face095, "/:shake"),
		Faceicon.fromResource(R.drawable.face096, "/:&lt;O&gt;"),
		Faceicon.fromResource(R.drawable.face097, "/:circle"),
		Faceicon.fromResource(R.drawable.face098, "/:kotow"),
		Faceicon.fromResource(R.drawable.face099, "/:turn"),
		Faceicon.fromResource(R.drawable.face100, "/:skip"),
		null
	};
}