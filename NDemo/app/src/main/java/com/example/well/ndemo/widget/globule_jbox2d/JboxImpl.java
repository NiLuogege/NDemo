package com.example.well.ndemo.widget.globule_jbox2d;

import android.view.View;

import com.example.well.ndemo.R;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Random;

/**
 * Created by ${LuoChen} on 2017/7/31 11:39.
 * email:luochen0519@foxmail.com
 */

public class JboxImpl {
    private float ViewWidth;
    private float ViewHeight;


    private World mWorld;
    private Random mRandom = new Random();
    private float mRatio = 50;
    private float mRestitution = 0.5f;//弹性系数(能量损失率)-->越大的话能量损失的越慢
    private float mDensity = 3;//刚体密度
    private float mFriction = 0.8f;//摩擦系数
    private float mDf = 2.0f / 60.0f;//迭代频率 ->这个值越大性能越低,我们这里就设置为Android的刷新频率就可以
    private int mVelocityIterations = 50;//速率迭代器
    private int mPositionIterations = 10;//迭代次数


    public JboxImpl(float viewWidth, float viewHeight) {
        ViewWidth = viewWidth;
        ViewHeight = viewHeight;
    }

    public void newWrold(float density) {
        if (mWorld == null) {
            mWorld = new World(new Vec2(0, 2.0f));//设置引力,我这里设置的很小
        }
        this.mDensity = density;
        setBorder();
    }

    /**
     * 给虚拟世界设置边界
     */
    private void setBorder() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.STATIC);//静止的

        PolygonShape shape = new PolygonShape();

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = mRestitution;
        fixtureDef.density = mDensity;
        fixtureDef.friction = mFriction;
        fixtureDef.shape = shape;


        setTopBorder(bodyDef, shape, fixtureDef);
        setBottomBorder(bodyDef, shape, fixtureDef);
        setLeftBorder(bodyDef, shape, fixtureDef);
        setRightBorder(bodyDef, shape, fixtureDef);
    }

    /**
     * 确定上边界
     *
     * @param bodyDef
     * @param shape
     * @param fixtureDef
     */
    private void setTopBorder(BodyDef bodyDef, PolygonShape shape, FixtureDef fixtureDef) {
        float widht = View2Jbox2d(ViewWidth);
        shape.setAsBox(widht, 1);
        bodyDef.setPosition(new Vec2(0, -1));//这里设置Y为-1是因为这个长方形的高度是1,为了避免长方形的高度影响效果
        Body top = mWorld.createBody(bodyDef);
        top.createFixture(fixtureDef);
    }

    /**
     * 确定下边界
     *
     * @param bodyDef
     * @param shape
     * @param fixtureDef
     */
    private void setBottomBorder(BodyDef bodyDef, PolygonShape shape, FixtureDef fixtureDef) {
        float widht = View2Jbox2d(ViewWidth);
        float height = View2Jbox2d(ViewHeight);
        shape.setAsBox(widht, 1);
        bodyDef.setPosition(new Vec2(0, height + 1));//这里加1是因为这个长方形的高度是1,为了避免长方形的高度影响效果
        Body bottom = mWorld.createBody(bodyDef);
        bottom.createFixture(fixtureDef);
    }

    /**
     * 确定左边界
     *
     * @param bodyDef
     * @param shape
     * @param fixtureDef
     */
    private void setLeftBorder(BodyDef bodyDef, PolygonShape shape, FixtureDef fixtureDef) {
        float height = View2Jbox2d(ViewHeight);
        shape.setAsBox(1, height);
        bodyDef.setPosition(new Vec2(-1, 0));//这里设置X为-1是因为这个长方形的宽度是1,为了避免长方形的宽度影响效果
        Body left = mWorld.createBody(bodyDef);
        left.createFixture(fixtureDef);
    }

    /**
     * 确定左边界
     */
    private void setRightBorder(BodyDef bodyDef, PolygonShape shape, FixtureDef fixtureDef) {
        float widht = View2Jbox2d(ViewWidth);
        float height = View2Jbox2d(ViewHeight);
        Vec2[] vec2s = new Vec2[3];
        vec2s[0] = new Vec2(widht / 3 * 2, 0);
        vec2s[1] = new Vec2(widht, 0);
        vec2s[2] = new Vec2(widht, height);
        shape.set(vec2s, 3);
        bodyDef.setPosition(new Vec2(0, 0));//因为上面我们设置的vec的集合都是以0,0点开始的所以这个三角形的位置应该也设置为0,0点
        Body right = mWorld.createBody(bodyDef);
        right.createFixture(fixtureDef);
    }

    /**
     * 创建球形刚体并和传入的view进行绑定
     *
     * @param view
     */
    public void createGlobule(View view) {
        JboxLayout.MyLayoutParams layoutParams = (JboxLayout.MyLayoutParams) view.getLayoutParams();
        boolean isGlobuleBody = layoutParams.getIsGlobuleBody();
        if (isGlobuleBody) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.setType(BodyType.DYNAMIC);//--->一会儿换成KINEMATIC ,换成KINEMATIC以后,小球会自己按照初始速度进行移动,不受我们的控制,感觉实现下雨或者下雪效果比较好
            bodyDef.setPosition(new Vec2(View2Jbox2d(ViewWidth / 2), 0));//设置小球的初始位置

            CircleShape circleShape = new CircleShape();
            int width = view.getWidth();
            float w = View2Jbox2d(width);
            circleShape.setRadius(w / 2);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.restitution = mRestitution;
            fixtureDef.density = mDensity;
            fixtureDef.friction = mFriction;
            fixtureDef.shape = circleShape;

            Body globuleBody = mWorld.createBody(bodyDef);
            globuleBody.createFixture(fixtureDef);
            globuleBody.setLinearVelocity(new Vec2(mRandom.nextFloat(), mRandom.nextFloat()));

            //进行绑定
            view.setTag(R.id.id_jbox2d_globule, globuleBody);
        }
    }


    private float View2Jbox2d(float f) {
        return f / mRatio;
    }

    private float Jbox2d2View(float f) {
        return f * mRatio;
    }

    /**
     * 让jbox2d开始一次计算
     */
    public void startDraw() {
        mWorld.step(mDf, mVelocityIterations, mPositionIterations);
    }

    /**
     * 注意这里要 减去View宽度的一半,因为要保证 中心点是在View的中点
     *
     * @param view
     * @return
     */
    public float getX(View view) {
        Body circleBody = (Body) view.getTag(R.id.id_jbox2d_globule);
        if (circleBody != null) {
            float x = circleBody.getPosition().x;
            float x_View = Jbox2d2View(x) - view.getWidth() / 2;
            return x_View;
        }
        return 0;
    }

    /**
     * 注意这里要 减去View高度的一半,因为要保证 中心点是在View的中点
     *
     * @param view
     * @return
     */
    public float getY(View view) {
        Body circleBody = (Body) view.getTag(R.id.id_jbox2d_globule);
        if (circleBody != null) {
            float y = circleBody.getPosition().y;
            float y_View = Jbox2d2View(y) - view.getHeight() / 2;
            return y_View;
        }
        return 0;
    }

    public float getRotation(View view) {
        Body circleBody = (Body) view.getTag(R.id.id_jbox2d_globule);
        if (circleBody != null) {
            float angle = circleBody.getAngle();
            return 180.0f / 3.14f * angle;
        }
        return 0;
    }

    public void onSensorChanged(View view, float x, float y) {
        Body circleBody = (Body) view.getTag(R.id.id_jbox2d_globule);
        if (circleBody != null) {
            circleBody.applyLinearImpulse(new Vec2(x, y), circleBody.getPosition(), true);
        }
    }
}
