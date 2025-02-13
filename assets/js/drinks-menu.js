const serials ={
    "fresh juices": "【新鮮果汁系列】",
    "smoothies": "【冰沙系列】",
    "specials": "【特調系列】",
    "mellow fresh milks": "【香醇鮮奶系列】",
    "hot drinks": "【熱飲系列】",
    "fine teas": "【茗茶系列】",
    "milk teas": "【奶茶系列】",
    "winter melons": "【冬瓜系列】",
    "yakult and polyphenols": "【多多/多酚系列】",
};

// Serials of fresh juices menu
const freshJuices = [
    "西瓜汁",
    "金桔檸檬",
    "甘蔗青茶",
    "鳳梨青茶",
    "檸檬原汁",
    "葡萄柚綠茶",
    "葡萄柚原汁",
    "西瓜牛奶",
    "木瓜牛奶",
    "熱帶水果青茶",
];

const freshJuicesSerials = freshJuices.map(juice => serials["fresh juices"] + juice);

// Serials of smoothies menu
const smoothies = [
    "綠豆沙",
    "檸檬冰沙",
    "酸梅冰沙",
    "草莓冰沙",
    "芒果冰沙",
    "百香果冰沙",
    "巧克力冰沙",
    "咖啡冰沙",
    "綠豆沙牛奶",
];

const smoothiesSerials = smoothies.map(smoothie => serials["smoothies"] + smoothie);

// Serials of specials menu
const specials = [
    "仙草蜜",
    "仙草干茶",
    "檸檬愛玉",
    "冰咖啡",
    "蛋密汁",
    "紫蘇梅綠茶",
    "蜂蜜蘆薈",
    "蜂蜜柚子茶",
    "百香雙響砲",
];

const specialsSerials = specials.map(special => serials["specials"] + special);

// Serials of mellow fresh milk menu
const mellowFreshMilks = [
    "紅茶拿鐵",
    "抹茶拿鐵",
    "香芋鮮奶",
    "杏仁鮮奶",
    "巧克力鮮奶",
    "阿華田鮮奶",
];

const mellowFreshMilksSerials = mellowFreshMilks.map(freshMilk => serials["mellow fresh milks"] + freshMilk);

// Serials of hot drinks menu
const hotDrinks = [
    "薑母茶",
    "桂圓茶",
    "沖繩奶茶",
    "薑母奶茶",
    "桂圓奶茶",
    "熱可可",
    "桂圓紅棗茶",
    "熱金桔檸檬",
    "薑汁柚子茶",
    "熱帶水果清茶",
    "地芋珍珠奶茶",
];

const hotDrinksSerials = hotDrinks.map(hotDrink => serials["hot drinks"] + hotDrink);

// Serials of fine teas menu
const fineTeas = [
    "麥香紅茶",
    "茉香綠茶",
    "四季青茶",
    "炭焙烏龍",
    "密香紅/綠茶",
    "梅子紅/綠茶",
    "薄荷紅/綠茶",
    "石榴紅/綠茶",
    "檸檬紅/綠茶",
    "鮮果百香紅/綠茶",
];

const fineTeasSerials = fineTeas.map(fineTea => serials["fine teas"] + fineTea);

// Serials of milk teas menu
const milkTeas = [
    "奶茶",
    "茉香奶茶",
    "烏龍奶茶",
    "珍珠奶茶",
    "胚芽奶茶",
    "布丁奶茶",
    "椰果奶茶",
    "黑磚奶茶",
    "沖繩奶茶",
    "泰式奶茶",
    "仙草凍奶茶",
    "地芋珍珠奶茶",
];

const milkTeasSerials = milkTeas.map(milkTea => serials["milk teas"] + milkTea);

// Serials of winter melons menu
const winterMelons = [
    "冬瓜茶",
    "冬瓜清茶",
    "冬瓜梅子",
    "冬瓜檸檬",
    "冬瓜仙草",
    "仙橙冬瓜",
    "冬瓜鮮奶",
];

const winterMelonsSerials = winterMelons.map(winterMelon => serials["winter melons"] + winterMelon);

// Serials of yakult and polyphenols menu
const yakultAndPolyphenols = [
    "多多綠茶",
    "多多檸檬",
    "多酚綠茶",
    "多酚檸檬",
    "多酚鮮奶",
];

const yakultAndPolyphenolsSerials = yakultAndPolyphenols.map(yakult => serials["yakult and polyphenols"] + yakult);

// Combine all the serials into menu
const drinksMenu = {
    "fresh juices": freshJuicesSerials,
    "smoothies": smoothiesSerials,
    "specials": specialsSerials,
    "mellow fresh milks": mellowFreshMilksSerials,
    "hot drinks": hotDrinksSerials,
    "fine teas": fineTeasSerials,
    "milk teas": milkTeasSerials,
    "winter melons": winterMelonsSerials,
    "yakult and polyphenols": yakultAndPolyphenolsSerials,
};

export default drinksMenu;