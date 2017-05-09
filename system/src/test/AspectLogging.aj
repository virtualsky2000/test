
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AspectLogging {

    @Around("execution(* *.*(..))")
    public Object getMethodExecTime(ProceedingJoinPoint point) throws Throwable {
        long methodStartTime = System.currentTimeMillis();

        String className = point.getSignature().toLongString();

        // 織り込み先(ジョインポイント)のメソッドを呼ぶ
        Object result = point.proceed();

        long execTime = System.currentTimeMillis() - methodStartTime;

        System.out.println(className + ":" + execTime);

        // 織り込み先(ジョインポイント)のメソッドの戻り値を返却
        return result;
    }

}
