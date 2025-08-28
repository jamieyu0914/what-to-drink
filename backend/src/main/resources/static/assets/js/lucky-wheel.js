$(document).ready(function () {
  //  Setup Variables
  var wheel = $(".wheel"),
    active = $(".active"),
    currentRotation,
    lastRotation = 0,
    tolerance,
    deg,
    $btnPlay = $("#ramdomButton"),
    $btnSlowMo = $("#menuButton");

  //  Random Degree
  function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }
  var deg = getRandomInt(360, 1080); // 可以多圈
  console.log(deg);

  //  Creating the Timeline
  var indicator = gsap.timeline(); // 指針
  var spinWheel = gsap.timeline(); // 轉盤
  indicator
    .to(active, 0.13, {
      rotation: -10, // 旋轉角度
      transformOrigin: "65% 36%", // 旋轉中心點
      ease: Power1.easeOut, // 緩動效果
    })
    .to(active, 0.13, { rotation: 3, ease: Power4.easeOut })
    .add("end");

  //  Lucky Wheel Animation
  spinWheel.to(wheel, 5, {
    rotation: deg, // 旋轉角度由亂數結果的變數 deg 決定
    transformOrigin: "50% 50%", // 旋轉中心點
    ease: Power4.easeOut, // 緩動效果
    onUpdate: function () {
      if (this.target && this.target[0] && this.target[0]._gsTransform) {
        currentRotation = Math.round(this.target[0]._gsTransform.rotation); //_gsTransform: current position of the wheel
        tolerance = currentRotation - lastRotation; // tolerance: 允許的變化範圍

        console.log("lastRot: " + lastRotation);
        console.log("currentRot: " + currentRotation);
        console.log("tol: " + tolerance);
        console.log(indicator.progress());
        console.log("spinwheelprogress: " + spinWheel.progress());

        if (Math.round(currentRotation) % (360 / 12) <= tolerance) { // 切分成 12 等分
          if (indicator.progress() > 0.2 || indicator.progress() === 0) {
            indicator.play(0);
          }
        }
      }
      lastRotation = currentRotation;
    },
  });
  spinWheel.add("end");
  //  Play Animation
  $btnPlay.click(function () { // 按下按鈕開始轉盤
    indicator.timeScale(1).seek(0); // 指針動畫
    spinWheel.timeScale(1).seek(0); // 轉盤動畫
  });
  //  Slow Motion
  $btnSlowMo.click(function () { // 按下按鈕慢速轉盤
    indicator.timeScale(0.2).seek(0.5); // 指針動畫, 播放速度是 0.2 倍, 指定從一半的位置開始
    spinWheel.timeScale(0.2).seek(0.5); // 轉盤動畫, 播放速度是 0.2 倍, 指定從一半的位置開始
  });
});
