package com.example.aircraftwar2024.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.annotation.NonNull;
import com.example.aircraftwar2024.ImageManager;
import com.example.aircraftwar2024.R;
import com.example.aircraftwar2024.aircraft.AbstractAircraft;
import com.example.aircraftwar2024.aircraft.AbstractEnemyAircraft;
import com.example.aircraftwar2024.aircraft.BossEnemy;
import com.example.aircraftwar2024.aircraft.HeroAircraft;
import com.example.aircraftwar2024.basic.AbstractFlyingObject;
import com.example.aircraftwar2024.bullet.AbstractBullet;
import com.example.aircraftwar2024.music.MyMediaPlayer;
import com.example.aircraftwar2024.music.MySoundPool;
import com.example.aircraftwar2024.playerDAO.Player;
import com.example.aircraftwar2024.factory.enemy_factory.BossFactory;
import com.example.aircraftwar2024.factory.enemy_factory.EliteFactory;
import com.example.aircraftwar2024.factory.enemy_factory.EnemyFactory;
import com.example.aircraftwar2024.factory.enemy_factory.MobFactory;
import com.example.aircraftwar2024.supply.AbstractFlyingSupply;
import com.example.aircraftwar2024.supply.BombSupply;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 游戏逻辑抽象基类，遵循模板模式，action() 为模板方法
 * 包括：游戏主面板绘制逻辑，游戏执行逻辑。
 * 子类需实现抽象方法，实现相应逻辑
 * @author hitsz
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    public static final String TAG = "BaseGame";
    boolean mbLoop; //控制绘画线程的标志位
    protected final SurfaceHolder mSurfaceHolder;
    protected Canvas canvas;  //绘图的画布
    protected final Paint mPaint;
    protected Player player;
    protected Handler handler;
    protected boolean isPaused = false;


    /**
     * Music
     */
    boolean isMusicOn;
    protected MyMediaPlayer bgmPlayer;
    protected MyMediaPlayer bossBgmPlayer;
    protected MySoundPool mySoundPool;
    public static final int SOUND_BULLET_HIT = 1;
    public static final int SOUND_BOMB_EXPLOSION = 2;
    public static final int SOUND_GET_SUPPLY = 3;
    public static final int SOUND_GAME_OVER = 4;

    //点击屏幕位置
    float clickX = 0, clickY=0;

    //窗口宽高
    int screenWidth, screenHeight;

    private int backGroundTop = 0;

    /**
     * 背景图片缓存，可随难度改变
     */
    protected Bitmap backGround;

    /**
     * 周期（ms)
     * 控制英雄机射击周期，默认值设为简单模式
     */
    protected double heroShootCycle = 10;
    private int heroShootCounter = 0;

    protected int tickCycle = Integer.MAX_VALUE;
    protected int tickCounter = 0;

    /**
     * 周期（ms)
     * 控制敌机射击周期，默认值设为简单模式
     */
    protected double enemyShootCycle = 20;
    private int enemyShootCounter = 0;

    /**
     * 游戏难度，敌机血量和速度的提升倍率，
     * 普通和困难模式随着时间增加而提高难度，提升普通和精英敌机的速度和血量
     */
    protected double gameLevel = 1.0;


    /**
     * 普通和困难模式中
     * 当得分每超过一次bossScoreThreshold，则产生一次boss机
     */
    protected int bossScoreThreshold = Integer.MAX_VALUE;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private final int timeInterval = 16;

    private final HeroAircraft heroAircraft;

    protected final List<AbstractEnemyAircraft> enemyAircrafts;
    private final List<AbstractFlyingSupply> flyingSupplies;

    private final List<AbstractBullet> heroBullets;
    private final List<AbstractBullet> enemyBullets;

    protected int enemyMaxNumber = 5;

    protected boolean gameOverFlag = false;
    private int score = 0;


    /**
     * 周期（ms)
     * 控制英雄机射击周期，默认值设为简单模式
     */
    private final int cycleDuration = 60;
    private int cycleTime = 0;
    protected double bossLevel = 1.0;

    /**
     * 产生小敌机和精英敌机。
     * 返回产生的敌机，可以为空链表（即表示不产生）；不能返回null！
     */
    private int enemyCounter = 0;
    protected double enemyCycle = 20;
    /**
     * 控制精英机产生概率，默认值设为简单模式
     */
    protected double eliteProb = 0.3;
    /**
     * 敌机工厂
     */
    private final EnemyFactory mobEnemyFactory;
    private final EnemyFactory eliteEnemyFactory;
    private final EnemyFactory bossEnemyFactory;
    private final Random random = new Random();

    public BaseGame(Context context, Handler handler, boolean isMusicOn){
        super(context);
        this.handler = handler;
        this.isMusicOn = isMusicOn;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        mbLoop = true;
        mPaint = new Paint();  //设置画笔
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        ImageManager.initImage(context);

        // 音乐
        bgmPlayer = new MyMediaPlayer(context, R.raw.bgm);
        bossBgmPlayer = new MyMediaPlayer(context, R.raw.bgm_boss);
        mySoundPool = new MySoundPool(context);

        if(isMusicOn) {
            bgmPlayer.play();
            // Load sounds
            mySoundPool.loadSound(R.raw.bullet_hit, SOUND_BULLET_HIT);
            mySoundPool.loadSound(R.raw.bomb_explosion, SOUND_BOMB_EXPLOSION);
            mySoundPool.loadSound(R.raw.get_supply, SOUND_GET_SUPPLY);
            mySoundPool.loadSound(R.raw.game_over, SOUND_GAME_OVER);
        }

        // 初始化英雄机
        heroAircraft = HeroAircraft.getHeroAircraft();
        heroAircraft.setHp(1000);

        enemyAircrafts = new CopyOnWriteArrayList<>();
        heroBullets = new CopyOnWriteArrayList<>();
        enemyBullets = new CopyOnWriteArrayList<>();
        flyingSupplies = new CopyOnWriteArrayList<>();

        mobEnemyFactory = new MobFactory();
        eliteEnemyFactory = new EliteFactory();
        bossEnemyFactory = new BossFactory();

        heroController();
    }
    private void heroShootAction() {
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }
    private void enemyShootAction() {
        // 敌机射击
        for (AbstractEnemyAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
    }
    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        //new Thread(new Runnable() {
        Runnable task = () -> {

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                // produceBoss 根据游戏难度策略产生 BOSS
                //根据游戏设定，随着时间提高游戏难度
                tick();
                enemyAircrafts.addAll(produceBoss());

                // produceEnemy 根据游戏难度策略产生精英敌机和小敌机
                enemyAircrafts.addAll(produceEnemy());
                if(shouldHeroShoot()){
                    heroShootAction();
                }

                if(shouldEnemyShoot()){
                    enemyShootAction();
                }

            }

            // 子弹移动
            bulletsMoveAction();
            // 飞机移动
            aircraftsMoveAction();

            suppliesMoveAction();

            // 撞击检测
            try {

                crashCheckAction();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 后处理
            postProcessAction();
        };
        task.run();
    }
    /**
     * 每个时刻均调用一次。
     * 普通和困难模式随着时间增加会提高游戏难度
     */
    protected abstract void tick();
    private void suppliesMoveAction() {
        for (AbstractFlyingSupply flyingSupply : flyingSupplies) {
            flyingSupply.forward();
        }
    }
    protected List<AbstractEnemyAircraft> produceBoss() {
        List<AbstractEnemyAircraft> res = new LinkedList<>();

        //当得分每超过一次bossScoreThreshold，且当前无boos机存在，则产生一次boss机
        // 普通模式boss机的血量不会变化
        if (this.getScore() >= bossScoreThreshold && !this.existBoss()) {
            if (isMusicOn) {
                bgmPlayer.pause();
                bossBgmPlayer.play();
            }

            bossScoreThreshold += bossScoreThreshold;
            res.add(bossEnemyFactory.createEnemyAircraft(bossLevel));
        }
        return res;
    }
    public int getScore() {
        return score;
    }
    private boolean existBoss() {
        for (AbstractEnemyAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft instanceof BossEnemy) {
                return true;
            }
        }
        return false;
    }

    private List<AbstractEnemyAircraft> produceEnemy() {
        enemyCounter++;
        if (enemyCounter >= enemyCycle) {
            // 每当计数次数达到周期，重置计数器，执行相关指令
            // 后射击周期同理
            enemyCounter = 0;
            List<AbstractEnemyAircraft> res = new LinkedList<>();

            // 产生敌机
            if (enemyAircrafts.size() < enemyMaxNumber) {
                if (random.nextDouble() < eliteProb) {
                    //精英机
                    res.add(eliteEnemyFactory.createEnemyAircraft(gameLevel));
                }
                else{
                    //普通敌机
                    res.add(mobEnemyFactory.createEnemyAircraft(gameLevel));
                }

            }
            return res;
        }
        return new LinkedList<>();
    }

    private boolean shouldHeroShoot() {
        heroShootCounter++;
        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            return true;
        }
        return false;
    }

    private boolean shouldEnemyShoot() {
        enemyShootCounter++;
        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            return true;
        }
        return false;
    }

    public void heroController(){
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clickX = motionEvent.getX();
                clickY = motionEvent.getY();

                if(Math.abs( heroAircraft.getLocationX() - clickX ) < 100 &&
                Math.abs( heroAircraft.getLocationY() - clickY ) < 100 && !isPaused) {
                    heroAircraft.setLocation(clickX, clickY);
                }
                if ( clickX<0 || clickX> screenWidth || clickY<0 || clickY>screenHeight){
                    // 防止超出边界
                    return false;
                }
                return true;
            }
        });
    }


    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void bulletsMoveAction() {
        for (AbstractBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (AbstractBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() throws InterruptedException {
        // 敌机子弹攻击英雄
        for (AbstractBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                // 播放子弹音效
                if(isMusicOn) {
                    mySoundPool.playSound(SOUND_BULLET_HIT);
                }
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (AbstractBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemyAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    if (enemyAircraft instanceof BossEnemy) {
                        bossBgmPlayer.stop();
                        bgmPlayer.play();
                    }
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 播放子弹音效
                    if(isMusicOn) {
                        mySoundPool.playSound(SOUND_BULLET_HIT);
                    }
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        //获得分数，产生道具补给
                        score += enemyAircraft.score();
                        flyingSupplies.addAll(enemyAircraft.generateSupplies());
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得补给
        for (AbstractFlyingSupply flyingSupply : flyingSupplies) {
            if (flyingSupply.notValid()) {
                continue;
            }
            if (heroAircraft.crash(flyingSupply) || flyingSupply.crash(heroAircraft)) {
                // 播放道具音效
                if (isMusicOn) {
                    if (flyingSupply instanceof BombSupply) {
                        mySoundPool.playSound(SOUND_BOMB_EXPLOSION);
                    }else {
                        mySoundPool.playSound(SOUND_GET_SUPPLY);
                    }
                }
                flyingSupply.activate();
                flyingSupply.vanish();
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        flyingSupplies.removeIf(AbstractFlyingObject::notValid);

        if (heroAircraft.notValid()) {
            gameOver();
        }

    }

    protected void gameOver(){
        gameOverFlag = true;

        if(isMusicOn) {
            bgmPlayer.release();
            bossBgmPlayer.release();
            mySoundPool.playSound(SOUND_GAME_OVER);
            //mySoundPool.release();
        }

        Log.i(TAG, "heroAircraft is not Valid");

        //获取当前时间
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        player = new Player("test",score,formatter.format(date));

        Message message = Message.obtain();
        message.what = 1;
        message.obj = player;
        handler.sendMessage(message);

        mbLoop = false;
    }

    public void pauseGame() {
        isPaused = true;
        if (isMusicOn) {
            bgmPlayer.pause();
            if(existBoss()){
                bossBgmPlayer.pause();
            }
        }
    }

    public void resumeGame() {
        isPaused = false;
        if (isMusicOn) {
            bgmPlayer.play();
            if(existBoss()){
                bossBgmPlayer.play();
            }
        }
    }

    public void draw() {
        canvas = mSurfaceHolder.lockCanvas();
        if(canvas == null){
            return;
        }

        if(!isPaused) {
            //绘制背景，图片滚动
            canvas.drawBitmap(backGround, 0, this.backGroundTop - backGround.getHeight(), mPaint);
            canvas.drawBitmap(backGround, 0, this.backGroundTop, mPaint);
            backGroundTop += 1;
            if (backGroundTop == screenHeight)
                this.backGroundTop = 0;
        }else{
            canvas.drawBitmap(backGround, 0, this.backGroundTop - backGround.getHeight(), mPaint);
            canvas.drawBitmap(backGround, 0, this.backGroundTop, mPaint);
        }

        //先绘制子弹，后绘制飞机
        paintImageWithPositionRevised(enemyBullets); //敌机子弹


        paintImageWithPositionRevised(heroBullets);  //英雄机子弹


        paintImageWithPositionRevised(enemyAircrafts);//敌机

        paintImageWithPositionRevised(flyingSupplies);//道具


        canvas.drawBitmap(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY()- ImageManager.HERO_IMAGE.getHeight() / 2,
                mPaint);

        //画生命值和分数
        paintScoreAndLife();

        paintBossLife();

        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    private void paintImageWithPositionRevised(List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            Bitmap image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            canvas.drawBitmap(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, mPaint);
        }
    }

    protected void paintScoreAndLife() {
        /**TODO:动态绘制文本框显示英雄机的分数和生命值**/
        // 设置画笔属性
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(60);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // 绘制分数
        canvas.drawText("Score: " + getScore(), 50, 100, mPaint);

        // 绘制生命值
        canvas.drawText("Life: " + heroAircraft.getHp(), 50, 200, mPaint);

        // 绘制血条
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(50,220,330,250, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(55,225,325,245, mPaint);
        mPaint.setColor(Color.RED);
        int right = 55 + ( 270 * heroAircraft.getHp() / heroAircraft.getMaxHp() );
        canvas.drawRect(55,225,right,245, mPaint);

    }

    private void paintBossLife() {
        if (this.existBoss()) {
            for( AbstractEnemyAircraft enemy:this.enemyAircrafts ) {
                if (enemy instanceof BossEnemy) {
                    // 绘制血条
                    mPaint.setColor(Color.BLACK);
                    canvas.drawRect(screenWidth-280,190,screenWidth-30,220, mPaint);
                    mPaint.setColor(Color.WHITE);
                    canvas.drawRect(screenWidth-275,195,screenWidth-35,215, mPaint);
                    mPaint.setColor(Color.RED);
                    int right = screenWidth-275 + ( 240 * enemy.getHp() / enemy.getMaxHp() );
                    canvas.drawRect(screenWidth-275,190,right,215, mPaint);
                    break;
                }
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        /*TODO*/
        mbLoop = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        screenWidth = i1;
        screenHeight = i2;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        /*TODO*/
        mbLoop = false;
    }

    @Override
    public void run() {
        /*TODO*/
        while(mbLoop) {
            if (!isPaused) {
                // 执行游戏逻辑
                action();
            }
            // 绘制游戏画面
            draw();
        }
    }
}
