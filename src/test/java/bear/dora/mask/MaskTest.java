package bear.dora.mask;

import bear.dora.mask.models.User;
import bear.dora.mask.utils.MaskUtils;
import com.google.gson.Gson;
import junit.framework.TestCase;

public class MaskTest extends TestCase {
    public void testMask() {
        Gson gson = new Gson();
        User user = User.builder()
                .username("Teikaiz")
                .password("12345678")
                .name("Gấu*Dora")
                .code("092094124")
                .build();
        System.out.println( gson.toJson(MaskUtils.getValueAfterMask(user)));
        System.out.println("====================");
        System.out.println( gson.toJson(user));
    }
}
