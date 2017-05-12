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
  for(int i = 0; i < 10; i++)
  {
    
  }
  __VERIFIER_assert(a == 0); 
  return 0; 
}