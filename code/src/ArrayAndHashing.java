import java.util.*;

public class ArrayAndHashing {

    // prefix sum with a hashmap and checking how many subarrays add up to k
    public static int prefixSum(int k, int[] nums) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        int sum = 0, ans = 0;
        for(int i : nums) {
            sum += i;
            ans += map.getOrDefault(sum - k, 0);
            map.merge(sum, 1, Integer::sum);
        }
        return ans;
    }

    public boolean containsDuplicate(int[] nums) {
        int ans = 0;
        for(int i : nums) {
            ans ^= i;
            if(ans == 0) return true;
        }
        return false;
    }

    public int majorityElement(int[] nums) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for(int num : nums) {
           if(map.merge(num, 1, Integer::sum) > nums.length/2) return num;
       }
       return -1;
    }
    public int maxSubArray(int[] nums) {
        int n = nums.length;
        int sum = 0, curr = nums[0];
        for(int num : nums) {
            sum += num;
            curr = Math.max(curr, sum);
            if(sum < 0) sum = 0;
        }
        return curr;
    }

    public Object[] maxSubArrayList(int[] nums) {
        int n = nums.length;
        List<Integer> list = new ArrayList<>();
        int sum = 0, curr = nums[0];
        for(int i = 0; i < n; i++) {
            sum += nums[i];
            list.add(nums[i]);
            curr = Math.max(curr, sum);
            if(sum < 0) {
                list.remove(nums[i]);
                sum = 0;
            }
        }
        return list.toArray();
    }
    public int majorityElementBruteForce(int[] nums) {
        int t = nums.length/2;
        for(int a : nums) {
            int f = 0;
            for(int b : nums) {
                if(b == a) f++;
            }
            if(f > t) return a;
        }
        return -1;
    }
    static void main(String[] args) {
        int[] array = {2,2,1,1,1,2,2};
        // System.out.println(prefixSum(4, array));
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int num : array) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        int ans = 0;
        for(int key : map.keySet()) {
            ans = Math.max(ans, map.get(key));
        }
        System.out.println(map);
        System.out.println(ans);
    }
}
