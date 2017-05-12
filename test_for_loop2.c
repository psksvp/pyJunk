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
  int i = 0;
  for(; i < 1000; i++)
  {
    
  }
  
  while(i < 2000)
  {
    i++;
  }
  __VERIFIER_assert(a == 0); 
  __VERIFIER_assert(i == 2000); 
  return 0; 
}