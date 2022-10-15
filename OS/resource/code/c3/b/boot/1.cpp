#include<iostream>
#include<functional>
using namespace std;

int main(int argc, char **argv)
{
    cout << argv[0] << endl;
    int b[] = {1024, 1, 1024 * 1024};
    int res = 0 ;
    while(1)
        for(auto m : b)
        {
            if( auto p = malloc(res + m ))
                free(p), res += m;
            else goto i;
        }
i:
    cout << (res + (1ll << 30) - 1) / (1 << 30)  << 'M' << endl;
    return 0;
    function<void(char * )> f = [](char *a) ->void {cout << a << endl;};
    f = [](char *a) ->void {cout << a << endl;} ;
    f("213123");
    return 0;
}