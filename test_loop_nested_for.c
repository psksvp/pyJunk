extern void __VERIFIER_error() __attribute__ ((__noreturn__));

void __VERIFIER_assert(int cond) {
  if (!(cond)) {
    ERROR: __VERIFIER_error();
  }
  return;
} 


int main(int argc, char** arg)
{ 
  int a = 0;
  for(int i = 0; i < 1000; i++)
  {
    for(int j = 0; j < 1000; j++)
    {
      a++;
    }  
  }
  
  __VERIFIER_assert(a == 1000000); 
  return 0; 
}